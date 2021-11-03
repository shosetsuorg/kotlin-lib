import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "app.shosetsu.lib"
version = "1.0.0"
description = "Kotlin library for shosetsu"

plugins {
	kotlin("jvm") version "1.5.31"
	id("org.jetbrains.dokka") version "1.5.31"
	kotlin("plugin.serialization") version "1.5.31"
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
	dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.5.31")

	// ### Core Libraries
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

	// java only
	implementation("org.jsoup:jsoup:1.14.3")
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.squareup.okhttp3:okhttp:4.9.2")
	implementation("com.google.guava:guava:31.0.1-jre")


	// ### Testing Libraries
	testImplementation(kotlin("stdlib"))
	testImplementation(kotlin("stdlib-jdk8"))

	testImplementation("net.java.dev.jna:jna:5.9.0") // for KTS

	testImplementation(kotlin("reflect"))
	testImplementation(kotlin("script-runtime"))
	testImplementation(kotlin("script-util"))
	testImplementation(kotlin("compiler-embeddable"))
	testImplementation(kotlin("scripting-compiler-embeddable"))
	testImplementation(kotlin("script-util"))

	testImplementation(kotlin("test"))
}


val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
	languageVersion = "1.5"
}