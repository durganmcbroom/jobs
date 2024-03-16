package com.durganmcbroom.jobs

public abstract class BasicJobFacetFactory<T : JobContext.Facet>(
    override val dependencies: List<JobContext.Key<out JobFacetFactory>> = ArrayList(),
    private val createElement: JobScope.() -> T,
) : JobFacetFactory {
    override fun <T> apply(job: Job<T>, oldContext: JobContext): Job<T> {
        return useContextFor(job) {
            createElement()
        }
    }
}