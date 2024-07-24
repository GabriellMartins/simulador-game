plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "com.br.minecraft"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
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
        options.release.set(8)
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.bstats", "com.br.minecraft.libs.bstats")
    }

    build {
        dependsOn(shadowJar)
    }
}
