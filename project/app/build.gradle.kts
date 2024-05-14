plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("plugin.serialization") version "1.4.21"
    id("com.google.protobuf") version "0.9.4"
}

android {
    namespace = "no.uio.ifi.in2000.team8"
    compileSdk = 34

    defaultConfig {
        applicationId = "no.uio.ifi.in2000.team8"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    //ktor
    val ktor_version = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-auth:$ktor_version")
    implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-gson:$ktor_version")
    implementation("io.ktor:ktor-client-json:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
    implementation("io.ktor:ktor-client-logging:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-serialization-jackson:$ktor_version")

    //Navigation
    val nav_version = "2.7.7"
    implementation("androidx.navigation:navigation-compose:$nav_version")

    //default
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    //Test
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    testImplementation("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:3.12.4")

    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    val compose_version = "1.0.5"

    //mapbox
    implementation("com.mapbox.maps:android:11.2.1")
    implementation("com.mapbox.extension:maps-compose:11.2.1")



    //material symbols
    implementation("androidx.compose.material:material-icons-extended:1.6.6")

    // proto data store
    implementation  ("androidx.datastore:datastore:1.0.0")
    implementation  ("com.google.protobuf:protobuf-javalite:3.21.7")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")


    //Splash screen
    implementation("androidx.core:core-splashscreen:1.2.0-alpha01")

}

protobuf{
    protoc{
        artifact = "com.google.protobuf:protoc:3.21.7"
    }
    plugins{
        generateProtoTasks{
            all().forEach {
                it.builtins {
                    create("java"){
                        option("lite")
                    }
                }
            }
        }
    }
}