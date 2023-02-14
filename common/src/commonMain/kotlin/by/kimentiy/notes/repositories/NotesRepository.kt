package by.kimentiy.notes.repositories

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.jvm.JvmInline

interface NotesRepository {

    val inboxTasks: Flow<List<InboxTask>>

    suspend fun updateInboxTask(newValue: InboxTask)

    suspend fun createInboxTask(
        title: String,
        description: String,
        subtasks: List<Subtask>
    ): InboxTask


    suspend fun getNotes(): Flow<List<Note>>

    suspend fun createNote(
        id: Long? = null,
        title: String,
        description: String,
        scn: Long = 0,
        lastModified: Instant? = null
    ): Note

    suspend fun updateNote(
        id: Id,
        title: String,
        description: String,
        scn: Long,
        lastModified: Instant? = null
    )

    suspend fun updateNote(
        id: Long,
        newId: Long? = null,
        scn: Long
    )


    fun getChecklists(): Flow<List<Checklist>>

    suspend fun updateChecklist(newValue: Checklist)

    suspend fun createChecklist(name: String, items: List<ChecklistItem>): Checklist


    suspend fun deleteById(id: Id)

    suspend fun getAllDeletedNotes(): List<DeletedNote>

    suspend fun clearDeletedNotes()
}

@JvmInline
@kotlinx.serialization.Serializable
value class Id(val id: Long)

interface Notelike {

    val id: Id
    val scn: Long
    val lastModified: Instant
}

data class Checklist(
    override val id: Id,
    val name: String,
    val items: List<ChecklistItem>,
    override val scn: Long,
    override val lastModified: Instant
) : Notelike

data class ChecklistItem(
    val title: String,
    val isChecked: Boolean
)

data class Note(
    override val id: Id,
    val title: String,
    val description: String,
    override val scn: Long,
    override val lastModified: Instant
) : Notelike

data class InboxTask(
    override val id: Id,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val subtasks: List<Subtask>,
    override val scn: Long,
    override val lastModified: Instant
) : Notelike

class Subtask(
    val title: String,
    val description: String,
    val isCompleted: Boolean
)

data class DeletedNote(
    override val id: Id,
    override val scn: Long,
    override val lastModified: Instant
) : Notelike