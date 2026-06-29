plugins {
    alias(libs.plugins.reset.androidLibraryCompose)
    alias(libs.plugins.reset.hilt)
}

android {
    namespace = "com.reset.core"
}

dependencies {
    implementation(libs.coroutines.core)

    // Shared MVI scaffolding (BaseViewModel) is built on Orbit; exposed as `api`
    // so feature modules inherit the Orbit types they extend.
    api(libs.orbit.core)
    api(libs.orbit.vm)
    implementation(libs.androidx.lifecycle.runtimeCompose)
}
