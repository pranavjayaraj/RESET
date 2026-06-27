import Constants.IMPLEMENTATION
import Constants.KAPT
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import reset.plugins.libs

class HiltPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.kapt")

                dependencies {
                    add(IMPLEMENTATION, libs.findLibrary("hilt-core").get())
                    add(KAPT, libs.findLibrary("hilt-compiler").get())
                }

                // For Android modules, also wire the Hilt Android plugin + runtime.
                withPlugin("com.android.base") {
                    apply("com.google.dagger.hilt.android")
                    dependencies {
                        add(IMPLEMENTATION, libs.findLibrary("hilt-base").get())
                        add(KAPT, libs.findLibrary("hilt-androidxCompiler").get())
                    }
                }
            }
        }
    }
}
