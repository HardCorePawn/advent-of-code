rootProject.name = "advent-of-code"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
include("src.main")
include("src:main")
findProject(":src:main")?.name = "main"
