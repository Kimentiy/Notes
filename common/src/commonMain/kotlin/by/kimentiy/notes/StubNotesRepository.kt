package by.kimentiy.notes

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

class StubNotesRepository : NotesRepository {

    private val _inboxTasks = MutableStateFlow(List(10) {
        InboxTask(
            id = Id(it.toLong()),
            title = "Title $it",
            description = "Descr $it",
            isCompleted = false,
            subtasks = emptyList(),
        )
    })
    override val inboxTasks: StateFlow<List<InboxTask>> = _inboxTasks

    override suspend fun getNotes(): Flow<List<Note>> {
        return flowOf(List(5) {
            Note(
                id = Id(it.toLong()),
                title = "Title$it",
                description = "Content$it"
            )
        })
    }

    override suspend fun createNote(title: String, description: String): Note {
        TODO("Not yet implemented")
    }

    override suspend fun updateNote(newValue: Note) {
        TODO("Not yet implemented")
    }

    override suspend fun getBuylist(): Checklist {
        return Checklist(
            id = Id(1),
            name = "Buylist",
            items = List(3) {
                ChecklistItem(
                    title = it.toString(),
                    isChecked = false
                )
            }
        )
    }

    override fun getChecklists(): Flow<List<Checklist>> {
        return flowOf(listOf(Checklist(
            id = Id(1234),
            name = "Buylist",
            items = List(3) {
                ChecklistItem(
                    title = it.toString(),
                    isChecked = false
                )
            }
        )))
    }

    override suspend fun updateChecklist(newValue: Checklist) {
        TODO("Not yet implemented")
    }

    override suspend fun createInboxTask(
        title: String,
        description: String,
        subtasks: List<Subtask>
    ): InboxTask {
        val newTask = InboxTask(
            id = Id(inboxTasks.value.size.toLong()),
            title = title,
            description = description,
            isCompleted = false,
            subtasks = subtasks
        )

        _inboxTasks.value = _inboxTasks.value + newTask

        return newTask
    }

    override suspend fun updateInboxTask(newValue: InboxTask) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteById(id: Id) {

    }
}