plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt) // <-- ajoute ceci
    kotlin("kapt") // pour la génération de code
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.vertlechemin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vertlechemin"
        minSdk = 28
        targetSdk = 35
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.navigation.compose.android)
    implementation(libs.androidx.animation.core.lint)
    implementation(libs.androidx.media3.exoplayer)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation("androidx.compose.material:material-icons-extended")
    implementation("org.osmdroid:osmdroid-android:6.1.13")
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("io.coil-kt:coil-gif:2.5.0")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.35.0-alpha")
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))
    implementation ("com.google.firebase:firebase-auth")
    implementation("androidx.compose.ui:ui:1.6.4")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.foundation:foundation:1.6.4")
    implementation("androidx.compose.ui:ui-text:1.6.4")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.4")





    // Pour utiliser ViewModel avec Compose + Hilt
    implementation(libs.hilt.navigation.compose)
}

kapt {
    correctErrorTypes = true
}