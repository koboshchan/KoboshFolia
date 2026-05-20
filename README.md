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