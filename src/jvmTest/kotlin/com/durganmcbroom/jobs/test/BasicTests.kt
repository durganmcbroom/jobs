package com.durganmcbroom.jobs.test

import com.durganmcbroom.jobs.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class BasicTests {
    class BasicFactory : JobElementFactory {
        override val key: JobElementKey<out JobElementFactory> = BasicFactory
        override val dependencies: List<JobElementKey<out JobElementFactory>>
            get() = listOf()

        override fun <T, E> apply(job: Job<T, E>): Job<T, E> {
            return Job {
                println("You basic")
                job()
            }
        }

        companion object : JobElementKey<BasicFactory> {
            override val name: String = "basic"
        }
    }

//    class BasicElement : JobElement {
//        public companion object : JobElementKey<BasicElement> {
//            override val name: String = "basic"
//        }
//
//        override val key: JobElementKey<*> = BasicElement
//    }

    @Test
    fun `Test job composition`() {
        runBlocking(BasicFactory()) {
            jobScope<Unit, Unit> {
                println("Job scope vs")
            }

            job<Unit, Unit> {
                println("Job")
            }
        }
    }
}