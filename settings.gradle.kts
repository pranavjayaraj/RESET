rootProject.name = "reset-app"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

include(":app")
include(":core")
include(":navigation")
include(":feature:home")
include(":feature:home:api")
include(":feature:sessions")
include(":feature:sessions:api")
include(":feature:settings")
include(":feature:settings:api")
include(":model")
include(":repository")
