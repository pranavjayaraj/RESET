import Constants.IMPLEMENTATION
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import reset.plugins.libs

class AndroidFeatureComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("reset.android.library.compose")
                apply("reset.hilt")
            }

            dependencies {
                add(IMPLEMENTATION, project(":core"))
                add(IMPLEMENTATION, libs.findLibrary("androidx-compose-ui").get())
                add(IMPLEMENTATION, libs.findLibrary("material3").get())
                add(IMPLEMENTATION, libs.findLibrary("hilt-navigationCompose").get())
                add(IMPLEMENTATION, libs.findLibrary("orbit-vm").get())
                add(IMPLEMENTATION, libs.findLibrary("orbit-compose").get())
            }
        }
    }
}
