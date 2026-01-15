package template.core.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface DispatcherSet {
    fun defaultDispatcher(): CoroutineDispatcher

    fun ioDispatcher(): CoroutineDispatcher

    fun mainDispatcher(): CoroutineDispatcher

    fun <T> runBlocking(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T,
    ): T
}
