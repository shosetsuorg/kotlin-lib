import org.gradle.jvm.tasks.Jar

group = "app.shosetsu.lib"
version = "1.0.0"
description = "Kotlin library for shosetsu"

plugins {
	kotlin("jvm") version "1.4.10"
	id("org.jetbrains.dokka") version "0.10.0"
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
	classifier = "javadoc"
}

repositories {
	jcenter()
	mavenCentral()
	maven("https://jitpack.io")
}

dependencies {
	implementation(kotlin("stdlib"))
	implementation("org.jsoup:jsoup:1.12.1")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.4.10")
	implementation("org.luaj:luaj-jse:3.0.1")
	implementation("org.json:json:20190722")
	implementation("com.squareup.okhttp3:okhttp:4.2.1")
	implementation("com.google.guava:guava:28.0-jre")
	testImplementation("junit:junit:4.12")
//	implementation("org.jetbrains.kolin:kotlin-test:v1.3.61")
}

