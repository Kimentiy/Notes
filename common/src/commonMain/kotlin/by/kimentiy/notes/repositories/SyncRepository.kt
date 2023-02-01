package by.kimentiy.notes.repositories

interface SyncRepository {

    suspend fun createNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun getNotes(): Result<List<Note>>

    suspend fun deleteNote()
}
