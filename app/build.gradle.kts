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
    implementation(project(":feature:home:api"))
    implementation(project(":feature:sessions"))
    implementation(project(":feature:sessions:api"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:settings:api"))
    implementation(project(":model"))
    implementation(project(":repository"))
    // The app host owns the real navigation graph (NavHost) and drives ObserveNavigation,
    // so it depends on navigation-compose directly rather than inheriting it from :navigation.
    implementation(libs.androidx.navigation.compose)
    // Branded launch: system splash themed via Theme.ResetApp.Starting, held until first frame.
    implementation(libs.androidx.splashscreen)
    implementation(libs.timber)
    // App-scope coroutine launch of the startup ReminderScheduler sync.
    implementation(libs.coroutines.android)
}
