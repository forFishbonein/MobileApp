plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.tutoring"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.tutoring"
        minSdk = 24
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.0"  // 或更高
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
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")    // OkHttp3 main library
    implementation("com.google.code.gson:gson:2.10")    // Optional: OkHttp logging interceptor
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    // Retrofit2 main library
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    // Retrofit2 Gson converter
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // accompanist-pager
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // accompanist-pager
    implementation("com.google.accompanist:accompanist-pager:0.28.0")
    // Render rich text
    implementation("com.google.accompanist:accompanist-webview:0.28.0")
    // Asynchronous loading of images
    implementation("io.coil-kt:coil-compose:2.2.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    // Compose UI testing
    androidTestImplementation(libs.androidx.ui.test.junit4)
    // OkHttp + MockWebServer testing
    androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.10.0")
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation("androidx.compose.foundation:foundation:1.4.0")
//    implementation("com.github.tehras:charts:1.0.0")
    implementation("com.github.tehras:charts:0.2.4-alpha")
}