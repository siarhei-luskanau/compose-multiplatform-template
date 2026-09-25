package template.core.database.room

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import template.core.database.DatabaseRecord
import template.core.database.room.entity.ExampleEntity
import template.core.database.room.repository.DatabaseRepositoryRoom
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AndroidRoomDatabaseProviderHostTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private lateinit var provider: AndroidRoomDatabaseProvider

    @Before
    fun setUp() {
        context.deleteDatabase(DB_NAME)
        provider = AndroidRoomDatabaseProvider(context)
    }

    @After
    fun tearDown() {
        provider.database.close()
        context.deleteDatabase(DB_NAME)
    }

    @Test
    fun database_shouldReturnSameInstance_onRepeatedAccess() {
        assertSame(provider.database, provider.database)
    }

    @Test
    fun database_shouldCreateFileInAppDatabasesDir_whenFirstWritten() =
        runBlocking {
            provider.database.exampleDao().upsert(ExampleEntity(id = "1", tag = "file"))

            assertTrue(context.getDatabasePath(DB_NAME).exists())
        }

    @Test
    fun database_shouldSaveAndReadRecord_viaDao() =
        runBlocking {
            val dao = provider.database.exampleDao()

            dao.upsert(ExampleEntity(id = "1", tag = "dao"))

            assertEquals(listOf(ExampleEntity(id = "1", tag = "dao")), dao.getAll().first())
        }

    @Test
    fun database_shouldSupportRepositoryRoundTrip() =
        runBlocking {
            val repository = DatabaseRepositoryRoom(provider)

            repository.save(DatabaseRecord(id = "1", tag = "first"))
            repository.save(DatabaseRecord(id = "2", tag = "second"))
            repository.save(DatabaseRecord(id = "1", tag = "updated"))
            repository.delete("2")

            assertEquals(listOf(DatabaseRecord(id = "1", tag = "updated")), repository.getAll().first())
        }

    @Test
    fun database_shouldPersistData_acrossProviderInstances() =
        runBlocking {
            provider.database.exampleDao().upsert(ExampleEntity(id = "1", tag = "persisted"))
            provider.database.close()
            provider = AndroidRoomDatabaseProvider(context)

            val result =
                provider.database
                    .exampleDao()
                    .getAll()
                    .first()

            assertEquals(listOf(ExampleEntity(id = "1", tag = "persisted")), result)
        }

    private companion object {
        const val DB_NAME = "cmp_template.db"
    }
}
