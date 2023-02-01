package by.kimentiy.notes.repositories

import kotlinx.coroutines.flow.Flow
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
        title: String,
        description: String
    ): Note

    suspend fun updateNote(newValue: Note)


    suspend fun getBuylist(): Checklist

    fun getChecklists(): Flow<List<Checklist>>

    suspend fun updateChecklist(newValue: Checklist)


    suspend fun deleteById(id: Id)
}

@JvmInline
value class Id(val id: Long)

interface WithGlobalId {

    val id: Id
}

data class Checklist(
    override val id: Id,
    val name: String,
    val items: List<ChecklistItem>
) : WithGlobalId

data class ChecklistItem(
    val title: String,
    val isChecked: Boolean
)

data class Note(
    override val id: Id,
    val title: String,
    val description: String
) : WithGlobalId

data class InboxTask(
    override val id: Id,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val subtasks: List<Subtask>
) : WithGlobalId

class Subtask(
    val title: String,
    val description: String,
    val isCompleted: Boolean
)
