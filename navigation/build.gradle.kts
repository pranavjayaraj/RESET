plugins {
    alias(libs.plugins.reset.androidLibraryCompose)
    alias(libs.plugins.reset.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.reset.navigation"
}

dependencies {
    // Kept as `implementation` (not `api`) on purpose: the NavController/NavHost types stay
    // internal to this module and the app host. Features depend on :navigation only for the
    // pure-Kotlin seam (Navigator, NavEvent, AppDestination) and must NOT see androidx types —
    // this keeps the feature layer platform-agnostic (KMP-ready). The app declares its own
    // navigation-compose dependency to use NavHost + ObserveNavigation.
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtimeCompose)
    implementation(libs.coroutines.core)
    // `api` on purpose: AppDestination classes are @Serializable, so their compiled signatures
    // reference kotlinx-serialization types. The runtime is pure Kotlin (KMP-safe), unlike the
    // androidx dependencies above which stay `implementation`.
    api(libs.kotlinx.serialization.core)
}
