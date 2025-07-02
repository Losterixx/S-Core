plugins {
    kotlin("jvm") version "2.1.20"
    id("com.gradleup.shadow") version "8.3.5"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "dev.losterixx"
version = "1.4"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.nexomc.com/releases")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("dev.dejvokep:boosted-yaml:1.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:1.7.3")

    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.nexomc:nexo:1.8.0")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
    compileOnly("net.luckperms:api:5.5")
}

tasks {
    runServer {
        minecraftVersion("1.21.5")
    }
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks {
    jar {
        enabled = false
    }
    shadowJar {
        archiveClassifier.set("")

        from("src/main/kotlin/dev/losterixx/sCore/utils/bStats/Metrics.java") {
            include("dev/losterixx/sCore/utils/bStats/**")
        }

        relocate("dev.dejvokep.boostedyaml", "dev.losterixx.sCore.libs")
    }
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

sourceSets {
    main {
        java {
            srcDirs("src/main/kotlin", "src/main/java")
        }
    }
}
