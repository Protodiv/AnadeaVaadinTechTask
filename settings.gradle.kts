rootProject.name = "TechTask"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    includeBuild("gradle/convention-plugins")

    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }

    versionCatalogs {
        create("ui") {
            from(files("gradle/ui.versions.toml"))
        }
    }
}

include("services:user")
include("services:auth")
include("services:gateway")

include("frontend:app")
include("frontend:data")
include("frontend:domain")
