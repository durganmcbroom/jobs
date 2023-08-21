plugins {
//    kotlin("multiplatform") version "1.8.20
//    "
    kotlin("jvm")// version "1.7.10"

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
//    jvm {
//        jvmToolchain(17)
//        withJava()
//        testRuns["test"].executionTask.configure {
//            useJUnitPlatform()
//        }
//
//        val main by compilations.getting {
//            compileKotlinTask.destinationDirectory.set(compileJavaTaskProvider!!.get().destinationDirectory.asFile.get())
//
//            compileJavaTaskProvider!!.get().run {
//                targetCompatibility = "17"
//                sourceCompatibility = "17"
//            }
//        }
//    }
//
//    sourceSets {
//        val jvmMain by getting {
//            dependencies {
//                implementation(project(":"))
//                implementation(project(":progress"))
//            }
//        }
//        val jvmTest by getting {
//            dependencies {
//                implementation(kotlin("test"))
//
//            }
//        }
//    }
//}
