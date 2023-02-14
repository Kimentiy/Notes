package by.kimentiy.notes.repositories

interface SyncRepository {

    suspend fun syncNotes(): Result<Unit>
}
