plugins {
    kotlin("multiplatform") version "1.8.20"
    kotlin("jvm") version "1.7.10" apply false
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.6.21"
}

group = "com.durganmcbroom"
version = "1.0-SNAPSHOT"

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
    js(BOTH) {
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }
    val hostOs = System.getProperty("os.name")
    val isMingwX64 = hostOs.startsWith("Windows")
    val nativeTarget = when {
        hostOs == "Mac OS X" -> macosX64("native")
        hostOs == "Linux" -> linuxX64("native")
        isMingwX64 -> mingwX64("native")
        else -> throw GradleException("Host OS is not supported in Kotlin/Native.")
    }

    
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {}
        val jsMain by getting
        val jsTest by getting
        val nativeMain by getting
        val nativeTest by getting
    }
}

tasks.register("publishAll") {
    dependsOn(allprojects.map { it.tasks.getByName("publish") })
}

tasks.register("publishAllLocally") {
    dependsOn(allprojects.map { it.tasks.getByName("publishToMavenLocal") })
}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

    tasks.register<Jar>("javadocJar") {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }
    publishing {
        repositories {
            maven {
                name = "yakclient-repo"
                url = uri("http://maven.yakclient.net/snapshots")
                isAllowInsecureProtocol = true

                credentials {
                    username = project.findProperty("maven.user") as String?
                    password = project.findProperty("maven.key") as String?
                }
                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}

publishing {
    publications.withType<MavenPublication> {
        artifact(tasks["javadocJar"])

        pom {
            name.set("Jobs")
            description.set("A job scheduling, management, and execution framework.")
            url.set("https://github.com/durganmcbroom/jobs")

            packaging = "jar"

            developers {
                developer {
                    id.set("durganmcbroom")
                    name.set("Durgan McBroom")
                }
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