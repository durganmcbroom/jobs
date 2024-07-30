package com.durganmcbroom.jobs

import com.durganmcbroom.jobs.async.AsyncJob

public interface JobFacetFactory : JobContext.Facet {
    override val key: JobContext.Key<out JobFacetFactory>
    override val factories: List<JobFacetFactory>
        get() = listOf(this)

    // Will apply factories in a topologically sorted list based on their dependencies and matching keys.
    public val dependencies: List<JobContext.Key<out JobFacetFactory>>

    // A convenience method used to apply extra context to a given job.
    public fun <T> useContextFor(job: Job<T>, context: JobScope.() -> JobContext): Job<T> = Job {
        job.call(context() + this.context)
    }

    // Should always use the above method ^ to apply context, manual application
    // with `wrapContext` could result in unintended stack overflows.
    public fun <T> apply(job: Job<T>, oldContext: JobContext): Job<T>

    public fun <T> apply(job: AsyncJob<T>, oldContext: JobContext): AsyncJob<T> {
        return AsyncJob {
            var mockedContext = oldContext
            val mockJob = Job {
                mockedContext = this.context

                Result.success(Unit)
            }

            apply(mockJob, oldContext).call(this.context).getOrThrow()

            job.call(mockedContext)
        }
    }
}


