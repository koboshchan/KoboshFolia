package com.kobosh.memory;

import ca.spottedleaf.moonrise.patches.fast_palette.FastHashMapPalette;
import it.unimi.dsi.fastutil.HashCommon;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathTypeCache;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.support.environment.VanillaFeature;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@VanillaFeature
class MemoryTablesTestSuite {
    private static Object field(Object target, Class<?> cls, String name) throws Exception {
        Field f = cls.getDeclaredField(name); f.setAccessible(true); return f.get(target);
    }

    @Test
    void palettePreservesIdentityDuplicatesResizeAndCopies() throws Exception {
        Object a = new String("same"), b = new String("same");
        var p = FastHashMapPalette.create(3, List.of(a, a, b));
        assertNull(field(p, p.getClass(), "valueToIdKeys"));
        assertEquals(0, p.idFor(a, (bits, value) -> fail()));
        assertEquals(2, p.idFor(b, (bits, value) -> fail()));
        var before = p.copy();
        for (int i=3; i<8; ++i) assertEquals(i,p.idFor(new Object(), (bits, value) -> fail()));
        assertEquals(0,p.idFor(a, (bits, value) -> fail()));
        assertNotNull(field(p, p.getClass(), "valueToIdKeys"));
        assertEquals(3,before.getSize());
        assertNull(field(before,before.getClass(),"valueToIdKeys"));
        var after = p.copy();
        assertNotSame(field(p,p.getClass(),"valueToIdKeys"),field(after,after.getClass(),"valueToIdKeys"));
        assertEquals(0,after.idFor(a,(bits,value)->fail()));
        assertEquals(999,p.idFor(new Object(),(bits,value)->{assertEquals(4,bits);return 999;}));
        for (int i=0;i<p.getSize();++i) assertSame(p.valueFor(i),after.valueFor(i));
    }

    @Test
    void paletteSerializationReadResetAndDuplicateRoundTrips() throws Exception {
        var ids = new net.minecraft.core.IdMapper<Object>();
        var entries = new ArrayList<Object>();
        for (int i=0;i<16;++i) { var value=new Object();ids.add(value);entries.add(value); }
        for (int bits=0;bits<=4;++bits) {
            final int originalBits=bits;
            var values=new ArrayList<Object>(entries.subList(0,1<<bits));
            if(bits>1) values.set(1,values.getFirst());
            var original=FastHashMapPalette.create(bits,values);
            var target=FastHashMapPalette.create(bits,entries.subList(0,1<<bits));
            // Initialize reverse tables before read, so read must clear old IDs.
            target.idFor(entries.getFirst(),(b,v)->fail());
            var buffer=new net.minecraft.network.FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
            try {
                original.write(buffer,ids);
                assertEquals(original.getSerializedSize(ids),buffer.readableBytes());
                target.read(buffer,ids);
                assertEquals(0,buffer.readableBytes());
                assertEquals(values.size(),target.getSize());
                for(int i=0;i<values.size();++i) assertSame(values.get(i),target.valueFor(i));
                assertEquals(0,target.idFor(entries.getFirst(),(b,v)->fail()));
                if(bits>1) assertEquals(1,target.idFor(entries.get(1),(b,v)->{ assertEquals(originalBits+1,b);return 1; }));
            } finally { buffer.release(); }
        }
    }

    @Test
    void paletteRandomIdentityReferenceModel() {
        var random = new java.util.Random(8675309);
        var entries=new ArrayList<Object>();
        for(int i=0;i<256;++i) entries.add(new Object());
        for(int round=0;round<40;++round) {
            var expected=new ArrayList<Object>();
            for(int i=0;i<random.nextInt(60);++i) expected.add(entries.get(random.nextInt(entries.size())));
            var palette=FastHashMapPalette.create(9,expected);
            for(int n=0;n<500;++n) {
                Object value=entries.get(random.nextInt(entries.size()));
                int index=expected.indexOf(value);
                if(index<0) {index=expected.size();expected.add(value);}
                assertEquals(index,palette.idFor(value,(b,v)->fail()));
                if(n%23==0) palette=palette.copy();
            }
            assertEquals(expected.size(),palette.getSize());
            for(int i=0;i<expected.size();++i) assertSame(expected.get(i),palette.valueFor(i));
        }
    }

    @Test
    void pathCacheInvalidationCollisionAndNewRegionLifecycle() throws Exception {
        var c = new PathTypeCache();
        var pos = new BlockPos(0,0,0);
        c.invalidate(pos);
        assertNull(field(c,PathTypeCache.class,"positions"));
        assertNull(field(c,PathTypeCache.class,"pathTypes"));
        var level = mock(BlockGetter.class);
        when(level.getBlockStateIfLoaded(any())).thenReturn(Blocks.AIR.defaultBlockState());
        assertEquals(PathType.OPEN,c.getOrCompute(level,pos));
        assertEquals(PathType.OPEN,c.getOrCompute(level,pos));
        verify(level,times(1)).getBlockStateIfLoaded(pos);
        when(level.getBlockStateIfLoaded(any())).thenReturn(null);
        c.invalidate(pos);
        assertEquals(PathType.BLOCKED,c.getOrCompute(level,pos));
        BlockPos collision = null;
        for(int x=1;x<100000;++x) {
            var candidate = new BlockPos(x,5,7);
            if (((int)HashCommon.mix(candidate.asLong()) & 4095)==0) {collision=candidate;break;}
        }
        assertNotNull(collision);
        when(level.getBlockStateIfLoaded(collision)).thenReturn(Blocks.AIR.defaultBlockState());
        assertEquals(PathType.OPEN,c.getOrCompute(level,collision));
        c.invalidate(pos); // A colliding, different key must not invalidate the replacement.
        assertEquals(PathType.OPEN,c.getOrCompute(level,collision));
        verify(level,times(1)).getBlockStateIfLoaded(collision);
        var nextRegion = new PathTypeCache();
        assertNull(field(nextRegion,PathTypeCache.class,"positions"));
    }

    @Test
    void shapeBoxesStayMutableAndMatchVoxelBoundsAfterMoveAndOptimization() {
        var shape=Shapes.or(Shapes.box(0,0,0,.25,1,1),Shapes.box(.5,0,0,1,.5,1));
        for(var s:List.of(Shapes.empty(),Shapes.block(),shape,shape.move(2,3,4),shape.optimize())) {
            List<AABB> expected=new ArrayList<>();
            s.forAllBoxes((x0,y0,z0,x1,y1,z1)->expected.add(new AABB(x0,y0,z0,x1,y1,z1)));
            assertEquals(expected,s.toAabbs());
            assertSame(s.toAabbs(),s.toAabbs());
            var list=s.toAabbs();var copy=new ArrayList<>(list);list.add(new AABB(0,0,0,1,1,1));list.removeLast();assertEquals(copy,list);
        }
    }
}
