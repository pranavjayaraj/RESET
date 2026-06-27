import Constants.ANDROID_TEST_IMPLEMENTATION
import Constants.IMPLEMENTATION
import Constants.TEST_IMPLEMENTATION
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import reset.plugins.configureAndroidCompose
import reset.plugins.configureApp
import reset.plugins.configureKotlinAndroid
import reset.plugins.libs

class AndroidApplicationComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }
            extensions.configure<ApplicationExtension> {
                configureAndroidCompose(this)
                configureApp(this)
                configureKotlinAndroid(this)
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
            }

            dependencies {
                add(IMPLEMENTATION, libs.findLibrary("androidx-compose-activity").get())
                add(IMPLEMENTATION, libs.findLibrary("androidx-compose-ui").get())
                add(IMPLEMENTATION, libs.findLibrary("androidx-compose-uiToolingPreview").get())
                add(IMPLEMENTATION, libs.findLibrary("material3").get())
                add(TEST_IMPLEMENTATION, libs.findLibrary("junit").get())
                add(ANDROID_TEST_IMPLEMENTATION, libs.findLibrary("androidx-test-junitExt").get())
                add(ANDROID_TEST_IMPLEMENTATION, libs.findLibrary("androidx-test-espressoCore").get())
            }
        }
    }
}
