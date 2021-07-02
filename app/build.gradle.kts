/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.util.Properties
import java.net.URI
import java.io.FileInputStream

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-android-extensions")
}

android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    signingConfigs {
        create("release") {

        }
    }

    compileSdkVersion(29)
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

    defaultConfig {
        applicationId = "ch.uzh.ifi.accesscomplete"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            // don't use proguard-android-optimize.txt, it is too aggressive, it is more trouble than it is worth
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
        }
        getByName("debug") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            applicationIdSuffix = ".debug"
        }
    }

    lintOptions {
        disable("MissingTranslation")
        isAbortOnError = false
    }
}


val keystorePropertiesFile = rootProject.file("keystore.properties")
if (keystorePropertiesFile.exists()) {
    val props = Properties()
    props.load(FileInputStream(keystorePropertiesFile))
    val releaseSigningConfig = android.signingConfigs.getByName("release")
    releaseSigningConfig.storeFile = file(props.getProperty("storeFile"))
    releaseSigningConfig.storePassword = props.getProperty("storePassword")
    releaseSigningConfig.keyAlias = props.getProperty("keyAlias")
    releaseSigningConfig.keyPassword = props.getProperty("keyPassword")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven { url = URI("https://jitpack.io") }
    maven { url = URI("https://dl.bintray.com/google/flexbox-layout/")}
    jcenter() {
        content {
            includeModule("org.sufficientlysecure", "html-textview") //this thing is no longer updated and stuck on jcenter.
            includeModule("com.mapzen.tangram", "tangram")
            /*
            A newer version of Tangram is available on Maven, but that means possibly getting a new API Key and redoing any offsets on quest markers, pins
            and highlightings. Especially the offsets broke when I last tried to update.
             */
        }
    }
}

configurations {
    // it's already included in Android
    all {
        exclude(group = "net.sf.kxml", module = "kxml2")
    }
}

dependencies {
    val kotlinVersion = "1.4.32"
    val mockitoVersion = "2.28.2"
    val kotlinxVersion = "1.4.3"
    val daggerVersion = "2.14.1"

    // tests
    testImplementation("junit:junit:4.12")
    testImplementation("org.mockito:mockito-core:$mockitoVersion")
    testImplementation("org.mockito:mockito-inline:$mockitoVersion")
    testImplementation("org.assertj:assertj-core:2.8.0")

    androidTestImplementation("androidx.test:core:1.4.0")
    androidTestImplementation("androidx.test:runner:1.4.0")
    androidTestImplementation("androidx.test:rules:1.4.0")
    androidTestImplementation("org.mockito:mockito-android:$mockitoVersion")
    androidTestImplementation("org.assertj:assertj-core:2.8.0")

    // dependency injection
    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")

    // Android stuff
    implementation("com.google.android.material:material:1.2.1")
    implementation("androidx.core:core-ktx:1.3.2")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.fragment:fragment-ktx:1.2.5")
    implementation("androidx.preference:preference-ktx:1.1.1")
    implementation("androidx.recyclerview:recyclerview:1.1.0")
    implementation("androidx.viewpager:viewpager:1.0.0")
    implementation("androidx.localbroadcastmanager:localbroadcastmanager:1.0.0")

    // photos
    implementation("androidx.exifinterface:exifinterface:1.3.2")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxVersion")

    // scheduling background jobs
    implementation("androidx.work:work-runtime:2.4.0")

    // finding in which country we are for country-specific logic
    implementation("de.westnordost:countryboundaries:1.5")
    // finding a name for a feature without a name tag
    implementation("de.westnordost:osmfeatures-android:2.1")
    // talking with the OSM API
    implementation("de.westnordost:osmapi-map:1.3")
    implementation("de.westnordost:osmapi-changesets:1.3")
    implementation("de.westnordost:osmapi-notes:1.2")
    implementation("de.westnordost:osmapi-user:1.2")

    // widgets
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("me.grantland:autofittextview:0.2.1")
    implementation("org.sufficientlysecure:html-textview:3.9") //needs to be removed until 2022 if it stays stuck on jcenter
    //implementation("com.duolingo.open:rtl-viewpager:2.0.0") deprecated on jcenter, no longer in use anyway
    implementation("com.google.android:flexbox:2.0.1")
    implementation("androidx.cardview:cardview:1.0.0")

    // ARCore
    implementation("com.google.ar.sceneform.ux:sceneform-ux:1.17.1")
    implementation("com.google.ar.sceneform:core:1.17.1")
    implementation("com.google.ar:core:1.21.0")

    // box2d view
    implementation("org.jbox2d:jbox2d-library:2.2.1.1")

    // serialization
    implementation("com.esotericsoftware:kryo:4.0.2")
    implementation("org.objenesis:objenesis:2.6")

    // map and location
    implementation("com.mapzen.tangram:tangram:0.13.0")

    // config files
    implementation("com.esotericsoftware.yamlbeans:yamlbeans:1.15")

    //API Calls and Database (used by Daniels stuff)
    val retrofitVersion = "2.9.0"
    val roomVersion = "2.3.0"
    val moshiVersion = "1.12.0"
    val lifecycle_version = "2.3.1"
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-moshi:$retrofitVersion")
    implementation ("com.squareup.moshi:moshi:$moshiVersion")
    kapt ("com.squareup.moshi:moshi-kotlin-codegen:$moshiVersion")
    //implementation ("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2") not needed anymore, you can just use suspend, which is supported by retrofit 2.6 and higher
    implementation("androidx.room:room-runtime:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion") // optional - Kotlin Extensions and Coroutines support for Room
    testImplementation("androidx.room:room-testing:$roomVersion") // optional - Test helpers
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    //implementation("com.squareup.okhttp3:logging-interceptor:3.5.0") //Only needed if you want to check what Retrofit is receiving

}

/** Localizations that should be pulled from POEditor etc. */
val bcp47ExportLanguages = setOf(
    "ast","ca","cs","da","de","el","en","en-AU","en-GB","es","eu","fa","fi","fr","gl","hr","hu",
    "id","it", "ja","ko","lt","ml","nb","no","nl","nn","pl","pt","pt-BR","ru","sk","sv","tr",
    "uk","zh","zh-CN","zh-HK","zh-TW"
)

tasks.register<UpdatePresetsTask>("updatePresets") {
    group = "accesscomplete"
    languageCodes = bcp47ExportLanguages
    targetDir = "$projectDir/src/main/assets/osmfeatures"
}

tasks.register<UpdateAppTranslationsTask>("updateTranslations") {
    group = "accesscomplete"
    languageCodes = bcp47ExportLanguages
    apiToken = properties["POEditorAPIToken"] as String
    targetFiles = { "$projectDir/src/main/res/values-$it/strings.xml" }
}

tasks.register<UpdateAppTranslationCompletenessTask>("updateTranslationCompleteness") {
    group = "accesscomplete"
    apiToken = properties["POEditorAPIToken"] as String
    targetFiles = { "$projectDir/src/main/res/values-$it/translation_info.xml" }
}

tasks.register<GenerateMetadataByCountry>("generateMetadataByCountry") {
    group = "accesscomplete"
    sourceDir = "$rootDir/res/country_metadata"
    targetDir = "$projectDir/src/main/assets/country_metadata"
}
