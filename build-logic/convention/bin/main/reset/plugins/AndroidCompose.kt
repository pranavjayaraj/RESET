package reset.plugins

import Constants
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

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

    // Treat read-only collections and Compose-free :model types as stable so leaf
    // composables stay skippable (see compose_stability.conf at the repo root).
    extensions.configure<ComposeCompilerGradlePluginExtension> {
        stabilityConfigurationFile.set(
            rootProject.layout.projectDirectory.file("compose_stability.conf"),
        )
        if (project.findProperty("composeReports") == "true") {
            val dir = layout.buildDirectory.dir("compose_reports")
            reportsDestination.set(dir)
            metricsDestination.set(dir)
        }
    }
}
