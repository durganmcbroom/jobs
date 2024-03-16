package com.durganmcbroom.jobs

public object EmptyJobContext : JobContext {
    override val factories: List<JobFacetFactory> = listOf()

    override fun <T : JobContext.Facet> get(key: JobContext.Key<T>): T? = null
    override fun minus(key: JobContext.Key<*>): JobContext = this
    override fun plus(other: JobContext): JobContext = other
    override fun iterator(): Iterator<JobContext.Facet> = object : Iterator<JobContext.Facet> {
        override fun hasNext(): Boolean = false

        override fun next(): JobContext.Facet = throw NoSuchElementException()
    }
}