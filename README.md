# KoboshFolia

KoboshFolia is a fork of Folia, a Minecraft server software.

## Features

1. Native 6 row ender chest support, similar to Purpur's ender chest support.
    We use permissions to handle ender chest size. Players with the `koboshfolia.enderchest.size.six` permission will have access to a 6-row ender chest, while those without it will have access to a standard 3-row ender chest.
2. Option to allow client side rng manipulation.
    This feature can be enabled with `allow-client-rng-manipulation` in `kobosh.yml`. When enabled, players will be able to manipulate the random number generator (RNG) on the client side, which can allow clients to predict enchantment outcomes and maybe other RNG-based mechanics in the game.
3. 

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
