plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.mychatapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.mychatapp"
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
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //Coil library
    implementation(libs.coil.compose.v260)

    //dataset
    implementation(libs.androidx.datastore.preferences)

    // Compose BOM để đồng bộ version
    implementation(platform(libs.androidx.compose.bom))

    // Core Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Material 3
    implementation(libs.material3)

    // Icons
    implementation(libs.material.icons.extended)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Activity Compose
    implementation(libs.androidx.activity.compose)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coil (hiển thị ảnh)
    implementation(libs.coil.compose)

    // Country & image picker (của bạn)
    implementation(libs.country.code.picker)
    implementation(libs.image.picker)

    // Unit tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
