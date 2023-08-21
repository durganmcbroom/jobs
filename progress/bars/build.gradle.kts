plugins {
    kotlin("jvm")
}

group = "com.durganmcbroom"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val runtime by configurations.creating


tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.durganmcbroom.jobs.progress.bars.BarProgressNotifierKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(
        runtime.map { if (it.isDirectory) it else zipTree(it) }.toSet()
    )
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":"))
    implementation(project(":progress"))
    runtime(kotlin("stdlib"))
    runtime(project(":"))
    runtime(project(":progress"))

}
kotlin {
    explicitApi()
}

publishing {
    publications.withType<MavenPublication> {
        artifact(tasks["javadocJar"])

        pom {
            packaging = "jar"

            developers {
                developer {
                    id.set("durganmcbroom")
                    name.set("Durgan McBroom")
                }
            }

            withXml {
                val repositoriesNode = asNode().appendNode("repositories")
                val yakclientRepositoryNode = repositoriesNode.appendNode("repository")
                yakclientRepositoryNode.appendNode("id", "yakclient")
                yakclientRepositoryNode.appendNode("url", "http://maven.yakclient.net/snapshots")
            }

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            scm {
                connection.set("scm:git:git://github.com/durganmcbroom/jobs")
                developerConnection.set("scm:git:ssh://github.com:durganmcbroom/jobs")
                url.set("https://github.com/durganmcbroom/jobs")
            }
        }
    }
}