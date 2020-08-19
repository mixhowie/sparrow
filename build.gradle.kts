plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.allopen")
    id("org.jetbrains.kotlin.plugin.noarg")
}

allprojects {
    repositories {
        mavenCentral()
    }

    apply(from = "$rootDir/config.gradle.kts")
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
    apply(plugin = "org.jetbrains.kotlin.plugin.noarg")

    java.sourceCompatibility = JavaVersion.VERSION_1_8
    java.targetCompatibility = JavaVersion.VERSION_1_8

    dependencies {
        // kotlin
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.2")
        implementation("com.fasterxml.jackson.core:jackson-core:2.11.2")

        // logback
        implementation("ch.qos.logback:logback-core:1.2.3")
        implementation("ch.qos.logback:logback-classic:1.2.3")

        // other
        implementation("joda-time:joda-time:2.10.6")
        implementation("org.apache.httpcomponents:httpclient:4.5.12")
        implementation("com.cronutils:cron-utils:9.0.1")
    }

    tasks {
        compileKotlin {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_1_8.toString()
                freeCompilerArgs = listOf("-Xjsr305=strict")
            }
        }
    }
}

configure(subprojects.filter { it.name != "sparrow-common" }) {
    dependencies {
        implementation(project(":sparrow-common"))
    }
}
