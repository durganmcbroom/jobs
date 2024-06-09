plugins {
    kotlin("multiplatform")
}

group = "com.durganmcbroom"

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvm {
        jvmToolchain(17)
        withJava()
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
    }
//    js(BOTH) {
//        browser {
//            commonWebpackConfig {
//                cssSupport {
//                    enabled.set(true)
//                }
//            }
//        }
//    }
//    val hostOs = System.getProperty("os.name")
//    val isMingwX64 = hostOs.startsWith("Windows")
//    val nativeTarget = when {
//        hostOs == "Mac OS X" -> macosX64("native")
//        hostOs == "Linux" -> linuxX64("native")
//        isMingwX64 -> mingwX64("native")
//        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
//    }


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":"))
                implementation(project(":logging"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(project(":progress:simple"))
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(tasks["javadocJar"])

        pom {
            packaging = "jar"
            artifactId = "jobs-$artifactId"

            developers {
                developer {
                    id.set("durganmcbroom")
                    name.set("Durgan McBroom")
                }
            }

            withXml {
                val repositoriesNode = asNode().appendNode("repositories")
                val extFrameworkRepositoryNode = repositoriesNode.appendNode("repository")
                extFrameworkRepositoryNode.appendNode("id", "extframework")
                extFrameworkRepositoryNode.appendNode("url", "http://maven.extframework.dev/snapshots")
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