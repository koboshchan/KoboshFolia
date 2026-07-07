pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "koboshfolia"

include("koboshfolia-api")
include("koboshfolia-server")

gradle.lifecycle.beforeProject {
    val mcVersion = providers.gradleProperty("mcVersion").get().trim()
    val koboshFoliaVersionChannel = providers.gradleProperty("channel").get().trim()
    val koboshFoliaBuildNumber = providers.environmentVariable("BUILD_NUMBER").orNull?.trim()
    val versionString = if (koboshFoliaBuildNumber == null) {
        "$mcVersion.local-SNAPSHOT"
    } else {
        "$mcVersion.build.$koboshFoliaBuildNumber-${koboshFoliaVersionChannel.lowercase()}"
    }
    version = versionString
}