plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("checkstyle")
}

group = "io.github.zrdzn.web.chattee"

allprojects {
    version = "1.0.0-SNAPSHOT"

    apply(plugin = "checkstyle")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    repositories {
        mavenCentral()

        maven {
            name = "reposilite-repository"
            url = uri("https://maven.reposilite.com/snapshots")
        }

        maven {
            name = "panda-repository"
            url = uri("https://repo.panda-lang.org/releases")
        }

        maven {
            name = "jitpack-repository"
            url = uri("https://jitpack.io")
        }
    }
}
