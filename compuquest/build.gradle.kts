plugins {
  kotlin("jvm") version "1.4.10"
  id("com.utopia-rise.godot-kotlin-jvm") version "0.2.0-3.3.2"
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.4.10")
  implementation("io.github.cdimascio:java-dotenv:5.1.3")

  api("com.fasterxml.jackson.core:jackson-databind:2.12.5")
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.12.5")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
  implementation(group = "org.jetbrains.kotlin", name = "kotlin-reflect", version = "1.4.10")
  implementation(group = "com.fasterxml.jackson.module", name = "jackson-module-afterburner", version = "2.12.5")
}

//kotlin.sourceSets.main {
////    kotlin.srcDirs("../godot/scripts")
//    kotlin.srcDirs("../godot/src/main/kotlin")
//}
