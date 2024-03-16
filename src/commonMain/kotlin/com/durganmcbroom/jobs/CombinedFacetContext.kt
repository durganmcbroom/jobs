package com.durganmcbroom.jobs

public class CombinedFacetContext(
    public val facets: Map<JobContext.Key<*>, JobContext.Facet>
) : JobContext {
    override val factories: List<JobFacetFactory> = facets.values.flatMap { it.factories }

    public constructor(contexts: List<JobContext.Facet>) : this(contexts.associateBy { it.key })

    override fun <T : JobContext.Facet> get(key: JobContext.Key<T>): T? {
        // Fast path, slow path
        return (facets[key] ?: facets.values.firstNotNullOfOrNull { it[key] }) as? T
    }

    override fun minus(key: JobContext.Key<*>): JobContext {
        if (!facets.containsKey(key)) return this
        return CombinedFacetContext(facets.toMutableMap().apply {
            remove(key)
        })
    }

    override fun plus(other: JobContext): JobContext {
        return if (other is JobContext.Facet) CombinedFacetContext(facets + mapOf(other.key to other))
        else CombinedFacetContext(this.facets + other.associateBy { it.key })
    }

    override fun iterator(): Iterator<JobContext.Facet> {
        return facets.values.iterator()
    }
}