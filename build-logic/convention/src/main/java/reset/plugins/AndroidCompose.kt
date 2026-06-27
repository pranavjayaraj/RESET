package reset.plugins

import Constants
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            add(Constants.IMPLEMENTATION, libs.findLibrary("androidx-compose-uiToolingPreview").get())
            add(Constants.DEBUG_IMPLEMENTATION, libs.findLibrary("androidx-compose-uiTooling").get())
            add(Constants.IMPLEMENTATION, libs.findLibrary("androidx-compose-runtime").get())
            add(Constants.IMPLEMENTATION, libs.findLibrary("androidx-compose-foundation").get())
        }

        composeOptions {
            useLiveLiterals = false
        }
    }
}
