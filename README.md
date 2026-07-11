<div align="center">

# KoboshFolia

KoboshFolia is a high-performance Minecraft server software, built as a fork of Folia. It optimizes server performance, implements select optimization patches from Lithium and Krypton, and offers highly configurable gameplay modifications.

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#building)
[![Java Version](https://img.shields.io/badge/Java-21%2B-blue)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-GPL--3.0-orange)](#thank-you)

</div>

---

## Features & Enhancements

KoboshFolia bundles performance enhancements and customizations directly into the server binary:

### Customizations & Mechanics

- **Gliding & Ground Lunge Improvements**: Configurable trident/gliding lunges, including customizable spear/elytra durability damage, boost multiplier adjustment, and minimum hunger requirements.
- **6-Row Ender Chests**: Native support for 6-row ender chests. Players with the `koboshfolia.enderchest.size.six` permission automatically access the full 6 rows.
- **Client-Side RNG Manipulation**: Option to allow clients to manipulate the random number generator (`allow-client-rng-manipulation` in `kobosh.yml`) for predicting enchantment outcomes.
- **Custom Server Commands**:
  - `/compass`: Displays a persistent compass boss bar indicating the player's direction.
  - `/uptime`: Displays server runtime duration in a configurable format.
- **Custom Limits & Scaling**:
  - Configurable barrel rows (1-6, default 3) via `barrel-rows`.
  - Configurable piston block push limit via `piston-block-push-limit`.
  - Configurable powered rail activation range via `powered-rail-activation-range`.
  - Configurable hunger starvation damage scaling via `hunger-starvation-damage`.
  - Infinity bow works without arrows in inventory (`infinity-bow-works-without-arrows`).

### Performance Optimizations

- **HUD Display**: Efficient real-time TPS, MSPT, memory, and region CPU utilization boss bar display.
- **Math & Physics**: Fused multiply-add (FMA) `lerp` in `Mth` ported from _SuperFastMath_.
- **Lithium Patches**:
  - _Pathfinding_: Optimized `PathNavigationRegion` chunk lookup using flat array section access, avoiding `EmptyLevelChunk` allocations.
  - _Redstone_: Optimized redstone wire power calculations by skipping checks for known-zero wire signals.
  - _Trigonometry_: Compact sine look-up table (16K entries instead of 64K) to improve CPU cache hits.
  - _Allocations_: Pre-allocated direction constants and static slot-arrays in `ComposterBlock` to eliminate GC pressure.
  - _Collisions_: Precomputed piston collision shapes (18 static shapes) and cached climbable checks on `LivingEntity`.
- **Krypton Patches**:
  - _Frame Decoding_: Optimized `Varint21FrameDecoder` using bitwise checks to find varint boundaries; avoids buffer copies with retained slices.
  - _Memory Allocation_: Reduced Netty arena allocation sizes from 16 MiB to 4 MiB for lower memory use and GC pressure.
  - _Channel Cleanup_: Immediate packet drop for inactive network channels inside `LegacyQueryHandler`.

---

## Configuration (`kobosh.yml`)

Configure KoboshFolia customizations through the `kobosh.yml` configuration file in your server directory:

| Key                                          | Default | Description                                                 |
| :------------------------------------------- | :------ | :---------------------------------------------------------- |
| `allow-client-rng-manipulation`              | `false` | Enables client-side RNG prediction mechanics.               |
| `default-ender-chest-rows`                   | `3`     | Default rows for standard ender chests.                     |
| `barrel-rows`                                | `3`     | Rows for barrels (supports `1` to `6`).                     |
| `piston-block-push-limit`                    | `12`    | Max number of blocks a piston can push.                     |
| `powered-rail-activation-range`              | `8`     | Range at which powered rails activate adjacent rails.       |
| `hunger-starvation-damage`                   | `1.0`   | Damage taken from starvation.                               |
| `infinity-bow-works-without-arrows`          | `false` | Allows infinity bows to shoot with an empty inventory.      |
| `lunge-improvement.enabled`                  | `false` | Enables configurable lunge improvements.                    |
| `lunge-improvement.spear-durability-damage`  | `3`     | Durability deducted from the spear on gliding lunge.        |
| `lunge-improvement.elytra-durability-damage` | `3`     | Durability deducted from the elytra on gliding lunge.       |
| `lunge-improvement.gliding-boost-multiplier` | `0.458` | Boost multiplier for gliding lunges.                        |
| `lunge-improvement.ground-min-hunger-level`  | `6`     | Minimum hunger level required to perform a lunge on ground. |

---

## Building

To compile and package KoboshFolia, ensure you have **Java 21 or higher** installed. Build the project using the wrapper scripts:

1. **Apply Patches**:
   ```bash
   ./patch.sh
   ```
2. **Build Server Jar**:
   ```bash
   ./gradlew build
   ```
3. **Assemble Paperclip**:
   ```bash
   ./gradlew createMojmapPaperclipJar
   ```

The compiled Paperclip executable will be located at:
`koboshfolia-server/build/libs/`

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
- [Krypton](https://github.com/astei/krypton) LGPL-3.0
