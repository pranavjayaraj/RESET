import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.hilt.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "reset.android.library"
            implementationClass = "AndroidLibraryPlugin"
        }
        register("androidLibraryCompose") {
            id = "reset.android.library.compose"
            implementationClass = "AndroidLibraryComposePlugin"
        }
        register("androidApplicationCompose") {
            id = "reset.android.application.compose"
            implementationClass = "AndroidApplicationComposePlugin"
        }
        register("androidFeatureCompose") {
            id = "reset.android.feature.compose"
            implementationClass = "AndroidFeatureComposePlugin"
        }
        register("hiltPlugin") {
            id = "reset.hilt"
            implementationClass = "HiltPlugin"
        }
    }
}
