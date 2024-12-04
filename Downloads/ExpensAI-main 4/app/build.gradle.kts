plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt") //XD added
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
}

android {
    namespace = "com.example.cmpt362_finalproject"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.cmpt362_finalproject"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
}

dependencies {
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.androidx.core.ktx)
    implementation(libs.mpandroidchart)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.room.common)
    implementation(libs.androidx.room.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.activity)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
    implementation ("com.prolificinteractive:material-calendarview:1.8.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.auth)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.javax.inject)
//    implementation("androidx.credentials:credentials:1.2.2")
//    implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
//    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Room components
    val room_version = "2.6.0"
    val lifecycle_version = "2.6.2"
    implementation(libs.androidx.room.ktx)
    kapt("androidx.room:room-compiler:$room_version")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx: $lifecycle_version")
}