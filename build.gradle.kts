import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "app.shosetsu.lib"
version = "1.0.0"
description = "Kotlin library for shosetsu"

plugins {
	kotlin("jvm") version "1.4.20"
	id("org.jetbrains.dokka") version "0.10.0"
	kotlin("plugin.serialization") version "1.4.20"
	maven
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

tasks.dokka {
	outputFormat = "html"
	outputDirectory = "$buildDir/javadoc"
}

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
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.20")

	// java only
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.squareup.okhttp3:okhttp:4.2.1")
	implementation("com.google.guava:guava:30.0-jre")

	// Cross platform confirmed
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
	testImplementation("org.jetbrains.kotlin:kotlin-test:1.4.20")
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	languageVersion = "1.4"
}