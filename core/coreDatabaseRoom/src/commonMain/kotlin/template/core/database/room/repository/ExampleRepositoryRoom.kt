package template.core.database.room.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import template.core.database.ExampleRecord
import template.core.database.ExampleRepository
import template.core.database.room.RoomDatabaseProvider
import template.core.database.room.entity.ExampleEntity

@Single
internal class ExampleRepositoryRoom(
    private val provider: RoomDatabaseProvider,
) : ExampleRepository {
    override fun getAll(): Flow<List<ExampleRecord>> =
        provider.database
            .exampleDao()
            .getAll()
            .map { list -> list.map { it.toDomain() } }

    override suspend fun save(record: ExampleRecord) = provider.database.exampleDao().upsert(record.toEntity())

    override suspend fun delete(id: String) = provider.database.exampleDao().deleteById(id)

    private fun ExampleEntity.toDomain() = ExampleRecord(id = id, tag = tag)

    private fun ExampleRecord.toEntity() = ExampleEntity(id = id, tag = tag)
}
