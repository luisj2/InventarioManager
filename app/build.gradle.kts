plugins {
    // Aplica el plugin de Android y Kotlin
    id("com.android.application") version "8.2.2"
    kotlin("android") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"

    // Firebase
    id("com.google.gms.google-services") version "4.4.2"
    id("org.jetbrains.kotlin.kapt")}

android {
    namespace = "com.xluis.inventarioefa"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.xluis.inventarioefa"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            // En Groovy sería `minifyEnabled false`, en Kotlin DSL es esto:
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
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            // Excluye licencias duplicadas
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    val room_version = "2.6.1"
    // BOM de Compose (gestiona versiones internas)
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))

    // Core Android
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")

    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")


    implementation("androidx.activity:activity-compose:1.10.1")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")  // para preview en tests

    // Navegación en Compose
    implementation("androidx.navigation:navigation-compose:2.8.2")

    // Lifecycle & ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")

    // Carga de imágenes
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Firebase (gestión de versiones con BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.7.3"))
    implementation("com.google.firebase:firebase-common-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")

    // Tests
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")


    // CameraX
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ML Kit - Barcode Scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // ✅ ZXing para generar códigos QR
    implementation("com.google.zxing:core:3.5.3")

    implementation("com.google.code.gson:gson:2.11.0")

    // ✅ Room (completo)
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")

    //IconDialogLib
    implementation ("com.maltaisn:icondialog:3.0.0")

    //Icons extended
    implementation("androidx.compose.material:material-icons-extended")

    //Datastore
    implementation ("androidx.datastore:datastore-preferences:1.1.7")

    //SWIPE REFRESH
    implementation ("com.google.accompanist:accompanist-swiperefresh:0.30.1")





}
