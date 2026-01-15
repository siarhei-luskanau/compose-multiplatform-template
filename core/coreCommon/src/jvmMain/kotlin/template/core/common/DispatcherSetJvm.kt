package template.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

internal class DispatcherSetJvm : DispatcherSet {
    override fun defaultDispatcher() = Dispatchers.Default

    override fun ioDispatcher() = Dispatchers.IO

    override fun mainDispatcher() = Dispatchers.Main

    override fun <T> runBlocking(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T,
    ): T =
        kotlinx.coroutines.runBlocking(
            context = context,
            block = block,
        )
}
