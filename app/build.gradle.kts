plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    // IMPORTANT: Add the KAPT plugin for future Room database integration (Unit IV)
    kotlin("kapt")
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.cropadvisorai"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.cropadvisorai"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx) // New: Fragment KTX

    // --- Syllabus Requirement: Coroutines (Unit II) & Lifecycle ---
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- AI Integration (Retrofit/Gemini API) ---
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp.logging)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid) // Optional but helpful for debugging network calls



    // --- Syllabus Requirement: Room (Unit IV - Placeholder for future implementation) ---
    // implementation("androidx.room:room-runtime:2.6.0")
    // kapt("androidx.room:room-compiler:2.6.0")
    // implementation("androidx.room:room-ktx:2.6.0")

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}