import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "app.shosetsu.lib"
version = "1.0.0"
description = "Kotlin library for shosetsu"

plugins {
	kotlin("jvm") version "1.5.0"
	id("org.jetbrains.dokka") version "1.4.32"
	kotlin("plugin.serialization") version "1.5.0"
	maven
}
tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }



val dokkaJar by tasks.creating(Jar::class) {
	group = JavaBasePlugin.DOCUMENTATION_GROUP
	description = "Assembles Kotlin docs with Dokka"
}

repositories {
	jcenter()
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.jsoup:jsoup:1.12.1")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.0")
	dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.4.32")

	// java only
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.squareup.okhttp3:okhttp:4.2.1")
	implementation("com.google.guava:guava:30.0-jre")
	implementation("net.java.dev.jna:jna:4.2.2")

	// Cross platform confirmed
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
	testImplementation("org.jetbrains.kotlin:kotlin-test:1.5.0")

	implementation(kotlin("reflect"))
	implementation(kotlin("script-runtime"))
	implementation(kotlin("script-util"))
	implementation(kotlin("compiler-embeddable"))
	implementation(kotlin("scripting-compiler-embeddable"))
	implementation(kotlin("script-util"))

}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	languageVersion = "1.5"
}