package by.kimentiy.notes.repositories

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface SettingsRepository {

    var lastSyncTime: Instant?

    fun addConflictNote(note: ConflictNote)

    fun getAllConflicts(): List<ConflictNote>

    fun removeConflictNote(id: Id)
}

@Serializable
data class ConflictNote(
    val id: Id,
    val serverVersion: NoteVersion,
    val localVersion: NoteVersion,
    val resultScn: Long
)

@Serializable
sealed class NoteVersion {

    @Serializable
    object Deleted : NoteVersion()

    @Serializable
    data class Version(
        val title: String,
        val description: String
    ) : NoteVersion()
}
