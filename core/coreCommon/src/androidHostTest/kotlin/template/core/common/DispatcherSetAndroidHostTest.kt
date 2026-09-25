package template.core.common

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.Dispatchers
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertSame

@RunWith(AndroidJUnit4::class)
class DispatcherSetAndroidHostTest {
    private val dispatcherSet: DispatcherSet = DispatcherSetAndroid()

    @Test
    fun defaultDispatcher_shouldReturnDispatchersDefault() {
        assertSame(Dispatchers.Default, dispatcherSet.defaultDispatcher())
    }

    @Test
    fun ioDispatcher_shouldReturnDispatchersIo() {
        assertSame(Dispatchers.IO, dispatcherSet.ioDispatcher())
    }

    @Test
    fun mainDispatcher_shouldReturnDispatchersMain() {
        assertSame(Dispatchers.Main, dispatcherSet.mainDispatcher())
    }
}
