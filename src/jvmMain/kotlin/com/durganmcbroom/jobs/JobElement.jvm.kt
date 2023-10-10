package com.durganmcbroom.jobs

import kotlinx.coroutines.CopyableThreadContextElement
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.CoroutineContext

//@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
//public actual fun <T : JobElement<T>> holdElement(curr: T): JobElementHolder<T> {
//    class CopyableJobElementHolder<I : JobElement<I>>(
//        override val inner: I
//    ) : CopyableThreadContextElement<Unit>, JobElementHolder<I> {
//        override val key: CoroutineContext.Key<*> by inner::key
//
//        override fun copyForChild(): CopyableThreadContextElement<Unit> {
//            return CopyableJobElementHolder(inner)
//        }
//
//        override fun mergeForChild(overwritingElement: CoroutineContext.Element): CoroutineContext {
//            check(overwritingElement is JobElementHolder<*>) {
//                "Cannot merge coroutine contexts, the overwriting element is not a JobElementHolder"
//            }
//            check(inner::class.isInstance(overwritingElement.inner)) {
//                "Cannot merge coroutine contexts, the inner type of the overwriting element: " +
//                        "'${overwritingElement.inner::class.java.name}' (a JobElementHolder) is not " +
//                        "the same type of this inner element: '${inner::class.java.name}'"
//            }
//
//            return CopyableJobElementHolder((overwritingElement.inner as I).compose(inner))
//        }
//
//        override fun updateThreadContext(context: CoroutineContext) {
//            // Nothing
//        }
//
//        override fun restoreThreadContext(context: CoroutineContext, oldState: Unit) {
//            // Nothing
//        }
//    }
//
//    return CopyableJobElementHolder(curr)
//}


