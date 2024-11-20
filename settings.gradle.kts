pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        maven("https://api.xposed.info")

        //maven("http://maven.aliyun.com/nexus/content/groups/public/")
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        //maven("http://maven.aliyun.com/nexus/content/groups/public/")
        maven("https://api.xposed.info")
    }
}

rootProject.name = "HyperStar"
include(":app")
 