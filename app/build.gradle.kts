plugins {
   alias(libs.plugins.android.application)
   alias(libs.plugins.jetbrains.kotlin.android)
   alias(libs.plugins.jetbrains.kotlin.compose)
   alias(libs.plugins.google.devtools.ksp)
   alias(libs.plugins.hilt.android)
}

android {
   namespace = "io.github.saidooubella.sash.run"
   compileSdk = 35

   defaultConfig {
      applicationId = "io.github.saidooubella.sash.run"
      minSdk = 21
      targetSdk = 35
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      }
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
   implementation(libs.androidx.lifecycle.runtime.compose)
   implementation(libs.androidx.lifecycle.viewmodel.compose)
   implementation(libs.androidx.activity.compose)
   implementation(libs.androidx.core.splashscreen)

   implementation(platform(libs.androidx.compose.bom))
   implementation(libs.androidx.ui)
   implementation(libs.androidx.ui.graphics)
   implementation(libs.androidx.ui.tooling.preview)
   implementation(libs.androidx.material3)
   implementation(libs.androidx.material.icons.extended)
   implementation(libs.androidx.material3.window.size)

   implementation(libs.kotlinx.collections.immutable)
   implementation(libs.coil.compose)

   implementation(libs.hilt.android)
   ksp(libs.hilt.compiler)

   implementation(libs.sash)
}