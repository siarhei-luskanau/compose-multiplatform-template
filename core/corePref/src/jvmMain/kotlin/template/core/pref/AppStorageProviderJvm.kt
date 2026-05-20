package template.core.pref

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import okio.FileSystem
import okio.Path.Companion.toPath
import org.koin.core.annotation.Single
import template.core.pref.StorageProvider
import java.io.File

@Single
internal class AppStorageProviderJvm : StorageProvider {
    override fun <T> getStorage(serializer: OkioSerializer<T>): Storage<T> {
        val storageFile =
            File(
                listOf(
                    System.getProperty("user.home"),
                    ".cmp-template",
                    "datastore",
                    "app.pref.json",
                ).joinToString(separator = File.separator),
            ).also { it.parentFile?.mkdirs() }
        return OkioStorage(
            fileSystem = FileSystem.SYSTEM,
            serializer = serializer,
            producePath = { storageFile.absolutePath.toPath() },
        )
    }
}
