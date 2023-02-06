package by.kimentiy.notes.repositories

interface SyncRepository {

    suspend fun syncNotes(): List<Note>
}
