package template.core.pref

import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import java.io.File

private val TEST_PREF_FILE = File(System.getProperty("java.io.tmpdir"), "test.app.pref.json")

@Single
internal class AppPrefPathProviderAndroid : PrefPathProvider {
    override fun get(): Path = TEST_PREF_FILE.absolutePath.toPath()
}

actual fun cleanUpTestStorage() {
    TEST_PREF_FILE.delete()
}
