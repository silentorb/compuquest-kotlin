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
}
//kotlin.sourceSets.main {
////    kotlin.srcDirs("../godot/scripts")
//    kotlin.srcDirs("../godot/src/main/kotlin")
//}
