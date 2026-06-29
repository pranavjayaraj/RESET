plugins {
    alias(libs.plugins.reset.androidLibrary)
    alias(libs.plugins.reset.hilt)
}

android {
    namespace = "com.reset.model"
}

dependencies {
    implementation(libs.coroutines.core)

    testImplementation(libs.junit)
}
