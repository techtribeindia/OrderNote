plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")


}

android {
    namespace = "com.project.ordernote"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.project.ordernote"
        minSdk = 24
        targetSdk = 33
        versionCode = 2
        versionName = "1.0.0"
        multiDexEnabled = true

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

    implementation ("com.github.bumptech.glide:glide:4.12.")

   implementation ("com.google.guava:guava:31.1-android")
    implementation ("com.google.android.gms:play-services-base:18.5.0")


    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation("androidx.activity:activity:1.9.1")
    implementation ("com.airbnb.android:lottie:4.2.0")
    implementation ("androidx.multidex:multidex:2.0.1")

    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation ("com.itextpdf:layout:7.1.14")
    implementation ("com.itextpdf:itextg:5.5.10")
    implementation ("androidx.work:work-runtime:2.8.0")
    implementation ("androidx.work:work-runtime-ktx:2.8.0")


     implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.google.firebase:firebase-crashlytics:19.1.0")
    implementation("com.google.firebase:firebase-messaging:24.0.1")

    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")
    implementation ("androidx.core:core:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-firestore:25.1.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}