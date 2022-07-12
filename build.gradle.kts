import org.gradle.jvm.tasks.Jar

group = "app.shosetsu.lib"
version = "1.0.0"
description = "Kotlin library for shosetsu"

plugins {
	kotlin("jvm") version "1.6.21"
	id("org.jetbrains.dokka") version "1.6.21"
	kotlin("plugin.serialization") version "1.6.21"
	maven
}

val dokkaJar by tasks.creating(Jar::class) {
	group = JavaBasePlugin.DOCUMENTATION_GROUP
	description = "Assembles Kotlin docs with Dokka"
}

java {
	sourceCompatibility = JavaVersion.VERSION_11
	targetCompatibility = JavaVersion.VERSION_11
}

repositories {
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	dokkaHtmlPlugin("org.jetbrains.dokka:kotlin-as-java-plugin:1.6.20")

	// ### Core Libraries
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

	// java only
	implementation("org.jsoup:jsoup:1.15.1")
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("com.squareup.okhttp3:okhttp:4.10.0")
	implementation("com.google.guava:guava:31.1-jre")


	// ### Testing Libraries
	testImplementation(kotlin("stdlib"))
	testImplementation(kotlin("stdlib-jdk8"))

	//testImplementation("net.java.dev.jna:jna:5.8.0") // for KTS

	testImplementation(kotlin("reflect"))
	//testImplementation(kotlin("script-runtime"))
	//testImplementation(kotlin("script-util"))
	//testImplementation(kotlin("compiler-embeddable"))
	//testImplementation(kotlin("scripting-compiler-embeddable"))
	//testImplementation(kotlin("script-util"))

	testImplementation(kotlin("test"))
}
