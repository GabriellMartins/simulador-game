plugins {
    java
    kotlin("jvm") version "1.8.21" // Ensure compatibility with Java 8
}

group = "com.br.minecraft"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly("org.projectlombok:lombok:1.18.32")

    implementation("redis.clients:jedis:5.1.2")

    compileOnly(files("C:\\Users\\Anderson\\Desktop\\Prisma Server\\lobby1\\PaperSpigot-1.8.8-R0.1-SNAPSHOT-latest.jar"))
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        archiveBaseName.set("simulador-game")
        archiveVersion.set("1.0.0-SNAPSHOT")
        manifest {
            attributes["Main-Class"] = "com.br.minecraft.SimuladorGame"
        }
    }

    build {
        dependsOn(jar)
    }
}
