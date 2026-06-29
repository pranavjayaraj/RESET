plugins {
    alias(libs.plugins.reset.androidLibrary)
    alias(libs.plugins.reset.hilt)
}

android {
    namespace = "com.reset.data"
}

dependencies {
    implementation(project(":model"))
    implementation(libs.coroutines.core)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
}
