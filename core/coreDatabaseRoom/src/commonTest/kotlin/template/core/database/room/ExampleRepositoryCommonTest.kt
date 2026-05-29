package template.core.database.room

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.koin.plugin.module.dsl.koinApplication
import template.core.database.ExampleRecord
import template.core.database.ExampleRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ExampleRepositoryCommonTest {
    @Test
    fun saveAndGetAll() =
        runTest {
            val koinApplication = koinApplication<TestKoinApplication>()
            val dao =
                koinApplication.koin
                    .get<RoomDatabaseProvider>()
                    .database
                    .exampleDao()
            dao.getAll().first().forEach { dao.deleteById(it.id) }
            val repository = koinApplication.koin.get<ExampleRepository>()

            repository.save(ExampleRecord(id = "1", tag = "test"))

            assertEquals(listOf(ExampleRecord(id = "1", tag = "test")), repository.getAll().first())
            koinApplication.close()
        }

    @Test
    fun deleteById() =
        runTest {
            val koinApplication = koinApplication<TestKoinApplication>()
            val dao =
                koinApplication.koin
                    .get<RoomDatabaseProvider>()
                    .database
                    .exampleDao()
            dao.getAll().first().forEach { dao.deleteById(it.id) }
            val repository = koinApplication.koin.get<ExampleRepository>()

            repository.save(ExampleRecord(id = "2", tag = "to-delete"))
            repository.delete("2")

            assertTrue(repository.getAll().first().isEmpty())
            koinApplication.close()
        }

    @Test
    fun saveUpdatesExistingRecord() =
        runTest {
            val koinApplication = koinApplication<TestKoinApplication>()
            val dao =
                koinApplication.koin
                    .get<RoomDatabaseProvider>()
                    .database
                    .exampleDao()
            dao.getAll().first().forEach { dao.deleteById(it.id) }
            val repository = koinApplication.koin.get<ExampleRepository>()

            repository.save(ExampleRecord(id = "3", tag = "original"))
            repository.save(ExampleRecord(id = "3", tag = "updated"))

            assertEquals(listOf(ExampleRecord(id = "3", tag = "updated")), repository.getAll().first())
            koinApplication.close()
        }
}
