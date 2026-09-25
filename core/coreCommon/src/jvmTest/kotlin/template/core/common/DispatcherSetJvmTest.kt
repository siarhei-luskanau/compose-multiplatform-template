package template.core.common

import kotlinx.coroutines.Dispatchers
import kotlin.test.Test
import kotlin.test.assertEquals

internal class DispatcherSetJvmTest {
    private val dispatcherSet = DispatcherSetJvm()

    @Test
    fun defaultDispatcher_shouldReturnDispatchersDefault() {
        assertEquals(Dispatchers.Default, dispatcherSet.defaultDispatcher())
    }

    @Test
    fun ioDispatcher_shouldReturnDispatchersIO() {
        assertEquals(Dispatchers.IO, dispatcherSet.ioDispatcher())
    }

    @Test
    fun mainDispatcher_shouldReturnDispatchersMain() {
        assertEquals(Dispatchers.Main, dispatcherSet.mainDispatcher())
    }
}
