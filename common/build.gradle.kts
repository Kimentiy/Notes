import kotlin.io.*

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "1.8.10"
    id("org.jetbrains.compose")
    id("app.cash.sqldelight") version "2.0.0-alpha05"
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        named("commonMain") {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)
                // Needed only for preview.
                implementation(compose.preview)

                // Kotlin json serializer
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")

                implementation("app.cash.sqldelight:primitive-adapters:2.0.0-alpha05")

                // navigation
                implementation("io.github.alexgladkov:odyssey-core:1.3.1")
                implementation("io.github.alexgladkov:odyssey-compose:1.3.1")

                // network
                implementation("io.ktor:ktor-client-core:2.2.3")

                // datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

                // key-value storage
                implementation("com.russhwolf:multiplatform-settings-no-arg:1.0.0")
            }
        }
        named("androidMain") {
            dependencies {
                api("androidx.appcompat:appcompat:1.4.2")
                api("androidx.core:core-ktx:1.8.0")
                implementation("app.cash.sqldelight:android-driver:2.0.0-alpha05")

                implementation("io.ktor:ktor-client-android:2.2.3")
            }
        }
        named("desktopMain") {
            dependencies {
                implementation("app.cash.sqldelight:sqlite-driver:2.0.0-alpha05")

                implementation("io.ktor:ktor-client-cio:2.2.3")
            }
        }
    }
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("by.kimentiy.notes")
        }
    }
}

tasks.create("cleanDb") {
    delete("${project.rootDir}/desktop/database")
}