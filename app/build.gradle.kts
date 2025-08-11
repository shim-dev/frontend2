plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}



android {
    namespace = "com.example.it_contest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.it_contest"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        ndk {
            // arm64-v8a: 최신 64비트 기기용
            // armeabi-v7a: 이전 32비트 기기용
            // x86_64: 64비트 에뮬레이터용
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a", "x86_64"))
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.android.volley:volley:1.2.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("commons-io:commons-io:2.11.0")
    implementation(platform("com.google.firebase:firebase-bom:34.0.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-storage")
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    //("androidx.health.connect:connect-client:1.2.0-alpha01")
    implementation ("io.github.bootpay:android:4.9.0")
    implementation("com.google.android.gms:play-services-location:21.2.0")
    implementation ("com.kakao.maps.open:android:2.12.8")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")
    implementation ("com.google.android.material:material:1.12.0")
    implementation ("androidx.appcompat:appcompat:1.7.0")

}