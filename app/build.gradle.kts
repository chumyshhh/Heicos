plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") version "1.9.23-1.0.20"
}

kotlin {
    sourceSets.debug {
        kotlin.srcDir("build/generated/ksp/debug/kotlin")
    }
    sourceSets.release {
        kotlin.srcDir("build/generated/ksp/release/kotlin")
    }
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

android {
    namespace = "com.heicos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.heicos"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "2.1.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.13"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"

        }
    }
    buildTypes {
        release {
            buildConfigField(
                type = "String",
                name = "baseUrl",
                value = "\"https://hentai-cosplay-xxx.com\""
            )
            buildConfigField(
                type = "String",
                name = "asianUrl",
                value = "\"https://porn-image.com\""
            )
        }
        debug {
            buildConfigField(
                type = "String",
                name = "baseUrl",
                value = "\"https://hentai-cosplay-xxx.com\""
            )
            buildConfigField(
                type = "String",
                name = "asianUrl",
                value = "\"https://porn-image.com\""
            )
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    //coil
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.5.0")
    //viewmodel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    //hilt
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.46.1")
    kapt("androidx.hilt:hilt-compiler:1.2.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    //navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("io.github.raamcosta.compose-destinations:animations-core:1.9.63")
    ksp("io.github.raamcosta.compose-destinations:ksp:1.9.63")
    implementation("com.google.accompanist:accompanist-navigation-material:0.32.0")
    //ui
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
    //jsoup
    implementation("org.jsoup:jsoup:1.16.1")
    //material
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material:1.6.7")
    //room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    //zoomable
    implementation("net.engawapg.lib:zoomable:1.6.0")
    //media3
    implementation("androidx.media3:media3-exoplayer:1.4.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.4.1")
    implementation("androidx.media3:media3-ui:1.4.1")
    implementation("androidx.media3:media3-exoplayer-hls:1.4.1")
    //ffmpeg
    implementation("com.arthenica:ffmpeg-kit-https:6.0-2")
    //ketch
    implementation("com.github.khushpanchal:Ketch:2.0.2")
    //foundation
    //implementation("androidx.compose.foundation:foundation:1.6.2")
}