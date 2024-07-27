plugins {
    id("com.android.application")
    id("com.google.gms.google-services")


}

android {
    namespace = "com.project.ordernote"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.ordernote"
        minSdk = 24
        targetSdk = 33
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
    buildFeatures {
        viewBinding = true
    }

}


dependencies {
    implementation ("com.github.bumptech.glide:glide:4.15.1")

    implementation("androidx.activity:activity:1.8.0")

    implementation ("com.airbnb.android:lottie:4.2.0")


    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("androidx.core:core:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:25.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}