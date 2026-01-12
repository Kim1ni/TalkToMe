@file:OptIn(ExperimentalWasmDsl::class)

import com.codingfeline.buildkonfig.compiler.FieldSpec
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.serialization)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.firebase.auth.ktx)
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:34.7.0"))

            implementation(libs.firebase.ai)

            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(libs.splash.screen)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)


            // Coil
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)

            // Lottie Animations
            implementation(libs.compottie)


            // Dependency Injection
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Firebase
            implementation(libs.firebase.app)
            implementation(libs.auth.firebase.kmp)
            implementation(libs.auth.kmp)
            implementation(libs.firebase.common)
            implementation(libs.firebase.storage)
            implementation(libs.firebase.firestore)

            // Simple notifier
            implementation(libs.messagebar.kmp)

            // Gson (for type converters)
            implementation(libs.gson)

            // Kotlin Date-Time
            implementation(libs.kotlinx.datetime)

            api(libs.kmp.notifier)

            implementation(libs.generativeai)

            implementation(libs.kotlinx.serialization)

            implementation(libs.navigation.compose)

            implementation(libs.androidx.navigation3.ui)
            //implementation(libs.androidx.lifecycle.viewmodel.navigation3)


            // Napier
            implementation(libs.napier)
            implementation(libs.vico.multiplatform)

            //Permissions
            api(libs.moko.permissions)
            api(libs.moko.permissions.compose)

        }

        appleMain.dependencies {
            // Ktor client dependency required for iOS
            implementation(libs.ktor.client.darwin)
        }


        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.kmp.talktome"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.kmp.talktome"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}


buildkonfig {
    packageName = "com.kmp.talktome"

    val properties = Properties().apply {
        load(project.rootProject.file("local.properties").reader())
    }
    val apiKey: String = properties.getProperty("apiKey")
    val model: String = properties.getProperty("model")
    val liveModel: String = properties.getProperty("liveModel")
    val webClientId: String = properties.getProperty("webClientId")


    require(apiKey.isNotEmpty()) {
        "Register your api key from developer and place it in local.properties as `apiKey`"
    }

    require(model.isNotEmpty()) {
        "Please pick a gemini model and place the name in in local.properties as `model`"
    }

    require(liveModel.isNotEmpty()) {
        "Please pick a gemini live model and place the name in in local.properties as `liveModel`"
    }

    require(webClientId.isNotEmpty()) {
        "Register your web client id from developer and place it in local.properties as `webClientId`"
    }


    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "GEMINI_API_KEY",
            apiKey
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "MODEL_NAME",
            model
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "LIVE_MODEL_NAME",
            liveModel
        )
        buildConfigField(
            FieldSpec.Type.STRING,
            "WEB_CLIENT_ID",
            webClientId
        )
    }
}
/*
// This custom task will compile the Kotlin code for all iOS simulator targets.
// If this task succeeds locally, the iOS part of your GitHub Actions build should also succeed.
tasks.register("checkIosBuild") {
    group = "Verification"
    description = "Checks if the shared KMP module can be built for iOS simulators."

    // Depend on the compilation tasks for the main iOS simulator architectures
    dependsOn("iosX64Binaries") // For Intel-based simulators (like in GitHub Actions)
    dependsOn("iosArm64Binaries") // For Apple Silicon-based simulators (modern Macs)
}*/