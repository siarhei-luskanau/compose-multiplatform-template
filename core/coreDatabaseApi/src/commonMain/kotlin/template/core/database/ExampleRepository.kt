package template.core.database

import kotlinx.coroutines.flow.Flow

interface ExampleRepository {
    fun getAll(): Flow<List<ExampleRecord>>

    suspend fun save(record: ExampleRecord)

    suspend fun delete(id: String)
}
