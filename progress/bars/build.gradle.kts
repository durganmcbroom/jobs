plugins {
    kotlin("jvm")
}

group = "com.durganmcbroom"

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