package com.durganmcbroom.jobs.test

import com.durganmcbroom.jobs.Job
import com.durganmcbroom.jobs.JobContext
import com.durganmcbroom.jobs.JobFacetFactory
import com.durganmcbroom.jobs.topologicalSort
import kotlin.test.Test

class TestFactoryTopoSort {
    data class TestKey(override val name: String) : JobContext.Key<JobFacetFactory>

    companion object {

        fun newFactory(key: TestKey, dependencies: List<JobContext.Key<out JobFacetFactory>>): JobFacetFactory {
            return object : JobFacetFactory {
                override val dependencies: List<JobContext.Key<out JobFacetFactory>> = dependencies
                override fun <T> apply(job: Job<T>, oldContext: JobContext): Job<T> {
                    return job
                }

                override val key: JobContext.Key<JobFacetFactory> = key
            }
        }
    }

    @Test
    fun `Test the topological sort`() {
        val factories = ArrayList<JobFacetFactory>()

        factories.add(newFactory(TestKey("One"), listOf(TestKey("Two"), TestKey("Three"))))
        factories.add(newFactory(TestKey("Two"), listOf(TestKey("Four"))))
        factories.add(newFactory(TestKey("Three"), listOf(TestKey("Five"))))
        factories.add(newFactory(TestKey("Four"), listOf(TestKey("Three"))))
        factories.add(newFactory(TestKey("Five"), listOf()))

        val sortedKeys = topologicalSort(factories)
            .map { it.key.name }

        println(sortedKeys)
        assert(
            sortedKeys ==
                    listOf("Five", "Three", "Four", "Two", "One")
        )
    }


}