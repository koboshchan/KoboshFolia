<div align="center">

# KoboshFolia

KoboshFolia is a fork of Folia, a high-performance Minecraft server software, optimized and enhanced with additional features, and customizations.

</div>

## Features

1. Native 6 row ender chest support, similar to Purpur's ender chest support.
    We use permissions to handle ender chest size. Players with the `koboshfolia.enderchest.size.six` permission will have access to a 6-row ender chest, while those without it will have access to a standard 3-row ender chest.
2. Option to allow client side rng manipulation.
    This feature can be enabled with `allow-client-rng-manipulation` in `kobosh.yml`. When enabled, players will be able to manipulate the random number generator (RNG) on the client side, which can allow clients to predict enchantment outcomes and maybe other RNG-based mechanics in the game.
3. TPS/memory/region boss bar display.
4. `/compass` command — toggles a boss bar compass showing the player's current direction.
5. `/uptime` command — shows how long the server has been running.
6. Configurable barrel rows (1–6, default 3) via `barrel-rows` in `kobosh.yml`.
7. Configurable piston push limit (default 12) via `piston-block-push-limit`.
8. Configurable powered rail activation range (default 8) via `powered-rail-activation-range`.
9. Configurable hunger starvation damage (default 1.0) via `hunger-starvation-damage`.
10. Infinity bow works without arrows in inventory, disabled by default (`infinity-bow-works-without-arrows`).
11. FMA (fused multiply-add) `lerp` in `Mth`, ported from SuperFastMath.
12. Lithium: optimized `PathNavigationRegion` chunk lookup — flat array + direct section access, avoids `EmptyLevelChunk` allocation.
13. Lithium: optimized redstone wire power calculations — fewer `getBlockState` calls by skipping known-zero wire signals.
14. Lithium: compact sine LUT (16K entries instead of 64K) — better CPU cache usage for `Mth.sin`/`cos`.
15. Lithium: pre-allocated `Direction.values()` constants in `PistonBaseBlock`, `PistonStructureResolver`, `RedStoneWireBlock` — avoids hot-loop array allocations.
16. Lithium: static slot-array constants in `ComposterBlock` — avoids `int[]` allocation on every hopper tick.
17. Lithium: precomputed piston collision shapes — 18 static shapes (3 offsets × 6 directions) eliminate `Shapes.or` allocations every tick during piston movement; non-standard offsets use a per-`VoxelShape` offset shape cache.
18. Lithium: cached `isPushable()` per tick on `LivingEntity` — avoids repeated `onClimbable()` evaluations when many entities push the same target in a dense crowd.
19. Krypton: optimized `Varint21FrameDecoder` — reads 4 bytes at once using bit tricks to locate the varint boundary; skips leading null bytes (prevents nullping); uses `readRetainedSlice` to avoid a buffer copy per packet.
20. Krypton: Netty allocator `maxOrder=9` — reduces pool arena size from 16 MiB to 4 MiB, matching Minecraft's 2 MiB max packet size for lower memory use and GC pressure.
21. Krypton: `LegacyQueryHandler` early-exit on inactive channels — discards packets immediately if the channel is no longer active.

## Building

To build KoboshFolia, you will need to have Java 21 or higher installed on your system. You can use the following command to build the project using Gradle:

```bash
./patch.sh
./gradlew build
./gradlew createMojmapPaperclipJar
```

The built JAR file will be located in `koboshfolia-server/build/libs`.

## Contributing

Read [CONTRIBUTING.md](CONTRIBUTING.md) for details on how to contribute to the project.

## Thank you

Thank you to everyone who has contributed to this project, whether through code, testing, or providing feedback. Your support is greatly appreciated!

These projects are referenced in the creation of KoboshFolia (in no particular order):
- [SuperFastMath](https://github.com/ItzjustElias/FastMathMod/) MIT
- [Paper](https://github.com/papermc/paper) GPL-3.0/MIT
- [Purpur](https://github.com/PurpurMC/Purpur) GPL-3.0/MIT
- [Luminol](https://github.com/LuminolMC/Luminol) GPL-3.0/MIT
- [Lithium](https://github.com/CaffeineMC/lithium-fabric) LGPL-3.0