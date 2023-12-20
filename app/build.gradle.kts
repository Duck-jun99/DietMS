plugins {
    id("com.android.application")
}


android {
    namespace = "com.mobilelec.dietms"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mobilelec.dietms"
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
}


dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // CardView
    implementation ("androidx.cardview:cardview:1.0.0")
    // Room
    implementation ("androidx.room:room-runtime:2.6.0")
    annotationProcessor ("androidx.room:room-compiler:2.6.0")
    testImplementation ("androidx.room:room-testing:2.6.0")

    //gson
    implementation ("com.google.code.gson:gson:2.9.0")

    //OkHttp
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")

    //Picasso
    implementation ("com.squareup.picasso:picasso:2.71828")

    implementation ("com.github.AnyChart:AnyChart-Android:1.1.5")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //Retrofit2
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.retrofit2:converter-scalars:2.9.0")
}