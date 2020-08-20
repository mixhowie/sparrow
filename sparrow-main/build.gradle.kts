version = "1.0.1"

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":sparrow-ssh"))
}

tasks {
    jar {
        dependsOn(shadowJar)
    }

    shadowJar {
        manifest {
            attributes["Main-Class"] = "com.sparrow.main.Main"
        }
    }
}
