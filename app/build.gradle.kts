plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.convertease"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.convertease"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        viewBinding = true
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("androidx.core:core-splashscreen:1.1.0-alpha02")
    implementation ("androidx.fragment:fragment:1.6.1")
    implementation("io.github.tutorialsandroid:filepicker:9.2.5")
    implementation ("androidx.activity:activity:1.8.0")
    implementation ("androidx.fragment:fragment:1.6.1")

}
