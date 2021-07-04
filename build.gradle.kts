import java.net.URI
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "me.oska.gw"
version = "1.0.4-SNAPSHOT"

plugins {
    java
    kotlin("jvm") version "1.5.10"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf("-Xjvm-default=compatibility")
}

repositories {
    mavenCentral()
    maven { url = URI.create("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8", "1.5.10"))

    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn(JavaPlugin.CLASSES_TASK_NAME)
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
    manifest {
        attributes(mapOf("Main-Class" to "$group/GoogleWhitelist"))
    }
}

val shadowJar = (tasks["shadowJar"] as ShadowJar).apply {}

val deployPath: String by project
val deployPlugin by tasks.registering(Copy::class) {
    dependsOn(shadowJar)

    System.getenv("PLUGIN_DEPLOY_PATH")?.let {
        from(shadowJar)
        into(it)
    }
}

val build = (tasks["build"] as Task).apply {
    arrayOf(sourcesJar, shadowJar, deployPlugin).forEach { dependsOn(it) }
}