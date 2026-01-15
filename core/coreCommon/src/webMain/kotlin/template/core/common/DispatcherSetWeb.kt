package template.core.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.coroutines.CoroutineContext

internal class DispatcherSetWeb : DispatcherSet {
    override fun defaultDispatcher() = Dispatchers.Default

    override fun ioDispatcher() = Dispatchers.Default

    override fun mainDispatcher() = Dispatchers.Main

    override fun <T> runBlocking(
        context: CoroutineContext,
        block: suspend CoroutineScope.() -> T,
    ): T =
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope
            .promise(
                context = context,
                block = block,
            ).unsafeCast()
}
