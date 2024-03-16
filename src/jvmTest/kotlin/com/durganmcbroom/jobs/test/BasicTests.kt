package com.durganmcbroom.jobs.test

import com.durganmcbroom.jobs.*
import java.lang.IllegalStateException
import kotlin.test.Test

class BasicTests {
    class BasicFactory : JobFacetFactory {
        override val key: JobContext.Key<BasicFactory> = BasicFactory
        override val dependencies: List<JobContext.Key<BasicFactory>>
            get() = listOf()

        override fun <T> apply(job: Job<T>): Job<T> {
            return Job {
                println("You basic")
                job.call(context)
            }
        }

        companion object : JobContext.Key<BasicFactory> {
            override val name: String = "basic"
        }
    }


    @Test
    fun `Test basic job composition`() {
        job(BasicFactory()) {
            job {
                println("Job scope vs")
            }().merge()

            job {
                val r = job {
                    "Inner job"
                }()
                println(r.merge())

                println("Job")
            }().merge()
        }.call(EmptyJobContext)
    }

    class TestIntFacet(
        val value: Int
    ) : JobContext.Facet {
        override val key: JobContext.Key<*> = TestIntFacet

        companion object : JobContext.Key<TestIntFacet> {
            override val name: String = "Int"
        }
    }

    class TestIntFacetFactory : BasicJobFacetFactory<TestIntFacet>(listOf(), {
        TestIntFacet((Math.random() * 1000).toInt())
    }) {
        override val key: JobContext.Key<out JobFacetFactory> = TestIntFacetFactory

        companion object : JobContext.Key<TestIntFacetFactory> {
            override val name: String = "Int facet factory"
        }
    }

    class TestStringFacet(
        val value: String
    ) : JobContext.Facet {
        override val key: JobContext.Key<*> = TestStringFacet

        companion object : JobContext.Key<TestStringFacet> {
            override val name: String = "String"
        }
    }

    class TestStringFacetFactory : BasicJobFacetFactory<TestStringFacet>(listOf(TestIntFacetFactory), {
        TestStringFacet("This is a facet with a random number: ${facet(TestIntFacet).value}")
    }) {
        override val key: JobContext.Key<out JobFacetFactory> = TestStringFacetFactory

        companion object : JobContext.Key<TestIntFacetFactory> {
            override val name: String = "Int facet factory"
        }
    }

    fun job2() = job(JobName("Second job")) {
        println("Job Name = '$jobName'")
    }

    fun job3() = job {
        println("Another int: '${facet(TestIntFacet).value}")
        println("And a string?! : '${facet(TestStringFacet).value}")
    }

    @Test
    fun `Test more complicated job composition`() {
        val theJob = launch(TestIntFacetFactory()) {
            val int = job2()()
                .merge()

            withContext(TestStringFacetFactory()) {
                job3()().merge()
            }
        }
    }

    @Test
    fun `Test out of order job factories in job composition`() {
        job {
            println("And a string?! : '${facet(TestStringFacet).value}")
        }.call(TestStringFacetFactory() + TestIntFacetFactory()).getOrThrow()
    }

    @Test
    fun `Test raw facets dont compose`() {
        launch(TestStringFacet("Hey how are you?") + TestIntFacetFactory()) {
            check(context[TestStringFacet]?.value == "Hey how are you?")

            job {
                check(context[TestStringFacet] == null)
                check(context[TestIntFacet] != null)
            }().merge()
        }
    }
}