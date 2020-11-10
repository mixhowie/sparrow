version = "1.0.1"

plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":sparrow-ssh"))
    implementation(project(":sparrow-http-monitor"))
}

tasks {
    jar {
        dependsOn(shadowJar)
    }

    shadowJar {
        manifest {
            attributes["Main-Class"] = "com.sparrow.main.Application"
        }
    }
}
