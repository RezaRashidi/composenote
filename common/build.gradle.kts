import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.squareup.sqldelight")
}

group = "com.rezarashidi"
version = "1.0-SNAPSHOT"

kotlin {
    android()
    jvm("desktop") {

        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        dependencies {

        }
    }
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    sourceSets {




        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.preview)
//                api(compose.uiTooling)
//                api(compose.materialIconsExtended)
//                api(compose.material3)
//                api(compose.animationGraphics)
//                api(compose.animation)
//                api(compose.ui)
                api ("ca.gosyer:compose-material-dialogs-core:0.6.6")
                api ("ca.gosyer:compose-material-dialogs-datetime:0.6.6")
                api("com.arkivanov.decompose:decompose:0.6.0")
                api("com.arkivanov.decompose:extensions-compose-jetbrains:0.6.0")
                implementation("com.squareup.sqldelight:coroutines-extensions:1.5.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")


            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
            dependencies {
                api("androidx.appcompat:appcompat:1.4.1")
                api("androidx.core:core-ktx:1.7.0")
                api("com.squareup.sqldelight:android-driver:1.5.3")

            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
            }
        }
        val desktopMain by getting {
            kotlin.srcDir("src/commonJvmAndroid/kotlin")
            dependencies {
//                api(compose.preview)
                api("com.squareup.sqldelight:sqlite-driver:1.5.3")
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(31)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(31)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
dependencies {
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.1")
}

sqldelight {
    database("TodoDatabase") {
        packageName = "com.rezarashidi.common"
//        schemaOutputDirectory =file("src/commonMain/kotlin/com/rezarashidi/common/sqldelight")
    }
    }
//    Database { // This will be the name of the generated database class.
//        packageName = "com.example"
//    }
