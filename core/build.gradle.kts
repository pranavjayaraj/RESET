plugins {
    alias(libs.plugins.reset.androidLibrary)
    alias(libs.plugins.reset.hilt)
}

android {
    namespace = "com.reset.core"
}

dependencies {
    implementation(libs.coroutines.core)
}
