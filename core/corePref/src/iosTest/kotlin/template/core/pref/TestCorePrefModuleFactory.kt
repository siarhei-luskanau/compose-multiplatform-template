package template.core.pref

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import platform.Foundation.NSFileManager
import platform.Foundation.NSTemporaryDirectory

@OptIn(ExperimentalForeignApi::class)
private val TEST_PREF_PATH = NSTemporaryDirectory() + "test.app.pref.json"

@Single
internal class AppPrefPathProviderIos : PrefPathProvider {
    override fun get() = TEST_PREF_PATH.toPath()
}

@OptIn(ExperimentalForeignApi::class)
actual fun cleanUpTestStorage() {
    NSFileManager.defaultManager.removeItemAtPath(TEST_PREF_PATH, error = null)
}
