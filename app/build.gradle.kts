plugins {
    alias(libs.plugins.reset.androidApplicationCompose)
    alias(libs.plugins.reset.hilt)
}

android {
    namespace = "com.reset.app"
    defaultConfig {
        applicationId = "com.reset.app"
        versionCode = 1
        versionName = "1.0.0"
        multiDexEnabled = true
    }
}

dependencies {
    implementation(project(":feature:home"))
    implementation(libs.timber)
}
