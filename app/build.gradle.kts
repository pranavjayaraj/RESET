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
    implementation(project(":core"))
    implementation(project(":navigation"))
    implementation(project(":feature:home"))
    implementation(project(":feature:settings"))
    implementation(project(":model"))
    implementation(project(":repository"))
    // The app host owns the real navigation graph (NavHost) and drives ObserveNavigation,
    // so it depends on navigation-compose directly rather than inheriting it from :navigation.
    implementation(libs.androidx.navigation.compose)
    implementation(libs.timber)
    // App-scope coroutine launch of the startup ReminderScheduler sync.
    implementation(libs.coroutines.android)
}
