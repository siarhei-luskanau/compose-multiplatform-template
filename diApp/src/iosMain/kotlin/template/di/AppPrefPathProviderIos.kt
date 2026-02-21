package template.di

import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import template.core.pref.PrefPathProvider

@Single
internal class AppPrefPathProviderIos : PrefPathProvider {
    @OptIn(ExperimentalForeignApi::class)
    override fun get(): Path =
        (
            NSFileManager.defaultManager
                .URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = false,
                    error = null,
                )?.path +
                Path.DIRECTORY_SEPARATOR +
                "app.pref.json"
        ).toPath()
}
