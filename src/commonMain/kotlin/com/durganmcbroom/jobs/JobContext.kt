package com.durganmcbroom.jobs

public interface JobContext : Iterable<JobContext.Facet> {
    public val factories: List<JobFacetFactory>

    public operator fun <T : Facet> get(key: Key<T>): T?

    public operator fun minus(key: Key<*>): JobContext

    public operator fun plus(other: JobContext): JobContext

    public interface Facet : JobContext {
        override val factories: List<JobFacetFactory>
            get() = listOf()

        public val key: Key<*>

        override fun <T : Facet> get(key: Key<T>): T? {
            return if (key == this.key) this as T
            else null
        }

        override fun minus(key: Key<*>): JobContext {
            return if (key == this.key) EmptyJobContext
            else this
        }

        override fun iterator(): Iterator<Facet> {
            return listOf(this).iterator()
        }

        public override operator fun plus(other: JobContext): JobContext {
            return if (other is Facet) CombinedFacetContext(listOf(this, other))
            else CombinedFacetContext(other.toList() + this)
        }
    }

    public interface Key<T : Facet> {
        public val name: String
    }
}

