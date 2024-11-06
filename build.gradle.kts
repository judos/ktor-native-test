val ktorVersion: String by project
val kotlinVersion: String by project
val logbackVersion: String by project

plugins {
	application
	kotlin("multiplatform") version "2.0.21"
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

kotlin {
	val hostOs = System.getProperty("os.name")
	val isArm64 = System.getProperty("os.arch") == "aarch64"
	val isMingwX64 = hostOs.startsWith("Windows")
	val nativeTarget = when {
		hostOs == "Mac OS X" && isArm64 -> macosArm64("native")
		hostOs == "Mac OS X" && !isArm64 -> macosX64("native")
		hostOs == "Linux" && isArm64 -> linuxArm64("native")
		hostOs == "Linux" && !isArm64 -> linuxX64("native")
		isMingwX64 -> mingwX64("native")
		else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
	}
	nativeTarget.apply {
		binaries {
			executable {
				entryPoint = "main"
			}
		}
	}
	sourceSets {
		val nativeMain by getting {
			dependencies {
				implementation("io.ktor:ktor-server-core:$ktorVersion")
				implementation("io.ktor:ktor-server-cio:$ktorVersion")
			}
		}
		val nativeTest by getting {
			dependencies {
				implementation(kotlin("test"))
				implementation("io.ktor:ktor-server-test-host:$ktorVersion")
			}
		}
	}
}
