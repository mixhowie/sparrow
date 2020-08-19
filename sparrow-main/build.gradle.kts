version = "1.0.1"

plugins {
    id("com.github.johnrengelman.shadow")
}

tasks {
    shadowJar {
        manifest {
            attributes["Main-Class"] = "com.sparrow.main.Main"
        }
    }
}