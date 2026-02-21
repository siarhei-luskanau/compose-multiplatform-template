package template.di

import kotlinx.coroutines.runBlocking
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import template.core.common.DispatcherSet
import template.core.pref.PrefPathProvider
import java.io.File

@Single
internal class AppPrefPathProviderJvm(
    private val dispatcherSet: DispatcherSet,
) : PrefPathProvider {
    override fun get(): Path =
        runBlocking(dispatcherSet.ioDispatcher()) {
            val file = File.createTempFile("temp_", "app.pref.json")
            file.absolutePath.toPath()
        }
}
