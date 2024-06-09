import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform") version "1.9.21"
//    kotlin("jvm") version "1.7.10" apply false
    id("maven-publish")
    id("org.jetbrains.dokka") version "1.9.10"
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

            testLogging {
                showStandardStreams = true
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {}
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {}
    }
}

tasks.register("publishAll") {
    dependsOn(allprojects.map { it.tasks.getByName("publish") })
}

tasks.register("publishAllLocally") {
    dependsOn(allprojects.map { it.tasks.getByName("publishToMavenLocal") })
}

//tasks.withType<KotlinCompile>().configureEach {
//    kotlinOptions {
//        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
//    }
//}

allprojects {
    apply(plugin = "maven-publish")
    apply(plugin = "org.jetbrains.dokka")

    version = "1.2-SNAPSHOT"

    val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

    tasks.register<Jar>("javadocJar") {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.outputDirectory)
    }
    publishing {
        repositories {
            maven {
                name = "extframeowrk-repo"
                url = uri("http://maven.extframework.dev/snapshots")
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