import org.jetbrains.kotlin.gradle.plugin.statistics.ReportStatisticsToElasticSearch.url

group "com.rezarashidi"
version "1.0-SNAPSHOT"
buildscript {
    dependencies {
        classpath("com.squareup.sqldelight:gradle-plugin:1.5.3")
        classpath ("org.jetbrains.kotlin:kotlin-serialization:1.6.10")

    }
}
allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
//        maven ("https://repo1.maven.org/maven2/")

    }

}

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
}

