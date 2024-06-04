plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.dagger.hilt)
    alias(libs.plugins.google.ksp)
}

android {
    namespace = "com.anhhoang.tipple.core.database"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
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
}

dependencies {
    implementation(project(":core:coroutines"))
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.datastore.pref)
    ksp(libs.androidx.room.compiler)
    implementation(libs.dagger.hilt)
    ksp(libs.dagger.hilt.compiler)

    testImplementation(libs.junit4)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.room.test)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.truth)
    testImplementation(libs.turbine)
}