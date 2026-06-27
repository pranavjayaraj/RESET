package reset.plugins

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

internal fun Project.configureApp(
    applicationExtension: ApplicationExtension,
) {
    applicationExtension.apply {
        val targetSdkVersion = (findProperty("targetSdkVersion") as? String)?.toIntOrNull()
        defaultConfig {
            targetSdk = targetSdkVersion ?: 34

            vectorDrawables {
                useSupportLibrary = true
            }
        }

        buildTypes {
            named("release") {
                isMinifyEnabled = false
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }
}
