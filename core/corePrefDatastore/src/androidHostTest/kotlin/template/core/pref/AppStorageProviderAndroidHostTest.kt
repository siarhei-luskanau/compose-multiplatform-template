package template.core.pref

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Storage
import androidx.datastore.core.readData
import androidx.datastore.core.use
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import template.core.common.DispatcherSet
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AppStorageProviderAndroidHostTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val prefFile: File get() = context.filesDir.resolve("app.pref.json")
    private val provider = AppStorageProviderAndroid(context = context, dispatcherSet = TestDispatcherSet)

    @Before
    fun setUp() {
        prefFile.delete()
    }

    @After
    fun tearDown() {
        prefFile.delete()
    }

    @Test
    fun getStorage_shouldReturnDefault_whenFileDoesNotExist() =
        runBlocking {
            val result = provider.getStorage().readPrefData()

            assertEquals(PrefData.DEFAULT, result)
            assertFalse(prefFile.exists())
        }

    @Test
    fun getStorage_shouldRoundTripPrefData_throughDataStore() =
        runBlocking {
            val job = SupervisorJob()
            val dataStore = DataStoreFactory.create(storage = provider.getStorage(), scope = CoroutineScope(Dispatchers.IO + job))

            dataStore.updateData { PrefData(key = SECRET) }
            val result = dataStore.data.first()
            job.cancelAndJoin()

            assertEquals(PrefData(key = SECRET), result)
        }

    @Test
    fun getStorage_shouldPersistEncryptedData_readableByNewConnection() =
        runBlocking {
            provider.getStorage().writePrefData(PrefData(key = SECRET))

            val result = provider.getStorage().readPrefData()

            assertEquals(PrefData(key = SECRET), result)
        }

    @Test
    fun getStorage_shouldNotWritePlaintext_toDisk() =
        runBlocking {
            provider.getStorage().writePrefData(PrefData(key = SECRET))

            val bytes = prefFile.readBytes()
            val content = bytes.decodeToString()

            assertTrue(bytes.isNotEmpty())
            assertFalse(content.contains(SECRET))
            assertFalse(content.contains("\"key\""))
        }

    @Test
    fun getStorage_shouldThrowCorruptionException_whenFileContainsPlaintextJson() =
        runBlocking {
            prefFile.parentFile?.mkdirs()
            prefFile.writeText("""{"key":"$SECRET"}""")

            assertFailsWith<CorruptionException> {
                provider.getStorage().readPrefData()
            }
            Unit
        }

    @Test
    fun getStorage_shouldThrowCorruptionException_whenDataWasEncryptedByAnotherProviderInstance() =
        runBlocking {
            provider.getStorage().writePrefData(PrefData(key = SECRET))
            val otherProvider = AppStorageProviderAndroid(context = context, dispatcherSet = TestDispatcherSet)

            assertFailsWith<CorruptionException> {
                otherProvider.getStorage().readPrefData()
            }
            Unit
        }

    private suspend fun Storage<PrefData>.readPrefData(): PrefData = createConnection().use { it.readData() }

    private suspend fun Storage<PrefData>.writePrefData(data: PrefData) {
        createConnection().use { connection -> connection.writeScope { writeData(data) } }
    }

    private object TestDispatcherSet : DispatcherSet {
        override fun defaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

        override fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

        override fun mainDispatcher(): CoroutineDispatcher = Dispatchers.Unconfined
    }

    private companion object {
        const val SECRET = "top-secret-value"
    }
}
