rootProject.name = "sparrow"

include("sparrow-common")
include("sparrow-main")
include("sparrow-ssh")
include("sparrow-http-monitor")

pluginManagement {
    apply(from = "$rootDir/config.gradle.kts")

    val kotlinVersion = extra["version.kotlin"].toString()

    plugins {
        id("org.jetbrains.kotlin.jvm").version(kotlinVersion)
        id("org.jetbrains.kotlin.plugin.allopen").version(kotlinVersion)
        id("org.jetbrains.kotlin.plugin.noarg").version(kotlinVersion)
        id("com.github.johnrengelman.shadow").version("5.0.0")
    }
}
