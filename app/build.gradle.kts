@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.bubbletastic.android.ping"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bubbletastic.android.ping"
        minSdk = 23
        targetSdk = 34
        versionCode = 5
        versionName = "1.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.wire)
    implementation(libs.otto)
    implementation(libs.androidx.swiperefreshlayout)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

//    androidTestImplementation 'com.crittercism.dexmaker:dexmaker:1.4'
//    androidTestImplementation 'com.crittercism.dexmaker:dexmaker-dx:1.4'
//    androidTestImplementation 'com.crittercism.dexmaker:dexmaker-mockito:1.4'
//    androidTestImplementation 'org.mockito:mockito-all:1.10.19'

    androidTestImplementation(libs.hamcrest.library)
//    androidTestImplementation("com.android.support.test:runner:0.4.1")
//    androidTestImplementation("com.android.support.test:rules:0.4.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-core:2.2.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-contrib:2.2.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-web:2.2.1")
    androidTestImplementation("com.android.support.test.espresso:espresso-idling-resource:2.2.1")

    //make tests use the same version of support-annotations as the main app to avoid conflicts. (removing this results in a compile time error regarding a conflict)
//    androidTestImplementation 'com.android.support:support-annotations:23.1.1'
}