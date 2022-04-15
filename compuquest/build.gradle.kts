plugins {
  kotlin("jvm") version "1.6.0" // Currently the highest Kotlin version supported by Godot-Kotlin is 1.6.0
  id("com.utopia-rise.godot-kotlin-jvm") version "0.3.3-3.4.2"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0")
  implementation("io.github.cdimascio:java-dotenv:5.1.3")

  api("com.fasterxml.jackson.core:jackson-databind:2.12.5")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.6.0")
  implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-afterburner", version = "2.12.5")

  api(group = "org.recast4j", name = "recast", version = "1.5.2")
  api(group = "org.recast4j", name = "detour", version = "1.5.2")
  api(group = "org.recast4j", name = "detour-crowd", version = "1.5.2")

  api("org.lwjgl:lwjgl:3.3.1")
  runtimeOnly("org.lwjgl:lwjgl:3.3.1:natives-windows")
  implementation("org.lwjgl:lwjgl-glfw:3.3.1")
  runtimeOnly("org.lwjgl:lwjgl-glfw:3.3.1:natives-windows")
  implementation("org.lwjgl:lwjgl-stb:3.3.1")
  runtimeOnly("org.lwjgl:lwjgl-stb:3.3.1:natives-windows")
  implementation("org.lwjgl:lwjgl-openal:3.3.1")
  runtimeOnly("org.lwjgl:lwjgl-openal:3.3.1:natives-windows")
}
//kotlin.sourceSets.main {
////    kotlin.srcDirs("../godot/scripts")
//    kotlin.srcDirs("../godot/src/main/kotlin")
//}
