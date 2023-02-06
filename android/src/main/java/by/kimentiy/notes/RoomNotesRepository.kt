package by.kimentiy.notes

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomNotesRepository(
    private val inboxTaskDao: InboxTaskDao,
    private val noteDao: NoteDao,
    private val checklistDao: ChecklistDao
) : NotesRepository {

    override val inboxTasks: Flow<List<InboxTask>>
        get() {
            val gson = Gson()
            return inboxTaskDao.getInboxTasks().map {
                it.map { entity ->
                    val parsedSubtasks =
                        gson.fromJson(entity.subtasksJson, Array<Subtask>::class.java)
                    InboxTask(
                        id = Id(entity.id),
                        title = entity.title,
                        description = entity.description,
                        isCompleted = entity.isCompleted,
                        subtasks = parsedSubtasks?.toList().orEmpty()
                    )
                }
            }
        }

    override suspend fun updateInboxTask(newValue: InboxTask) {
        withContext(Dispatchers.IO) {
            val gson = Gson()
            inboxTaskDao.updateInboxTask(
                InboxTaskEntity(
                    id = newValue.id.id,
                    title = newValue.title,
                    description = newValue.description,
                    isCompleted = newValue.isCompleted,
                    subtasksJson = gson.toJson(newValue.subtasks)
                )
            )
        }
    }

    override suspend fun createInboxTask(
        title: String,
        description: String,
        subtasks: List<Subtask>
    ): InboxTask = withContext(Dispatchers.IO) {
        val gson = Gson()
        val id = inboxTaskDao.addInboxTask(
            InboxTaskEntity(
                id = getNewGlobalId(),
                title = title,
                description = description,
                isCompleted = false,
                subtasksJson = gson.toJson(subtasks)
            )
        )

        InboxTask(
            id = Id(id),
            title = title,
            description = description,
            isCompleted = false,
            subtasks = subtasks
        )
    }

    override suspend fun getNotes(): Flow<List<Note>> = withContext(Dispatchers.IO) {
        noteDao.getNotes().map {
            it.map { entity ->
                Note(
                    id = Id(entity.id!!),
                    title = entity.title,
                    description = entity.description
                )
            }
        }
    }

    override suspend fun createNote(title: String, description: String): Note =
        withContext(Dispatchers.IO) {
            val id = noteDao.addNote(
                NoteEntity(
                    id = getNewGlobalId(),
                    title = title,
                    description = description
                )
            )

            Note(
                id = Id(id),
                title = title,
                description = description
            )
        }

    override suspend fun updateNote(newValue: Note) = withContext(Dispatchers.IO) {
        noteDao.updateNote(
            NoteEntity(
                id = newValue.id.id,
                title = newValue.title,
                description = newValue.description
            )
        )
    }

    override suspend fun getBuylist(): Checklist = withContext(Dispatchers.IO) {
        var buylist = checklistDao.getBuylist()
        if (buylist == null) {
            buylist = ChecklistEntity(
                id = 1,
                name = "Buylist",
                itemsJson = ""
            )
            val id = checklistDao.addChecklist(buylist)
            buylist = buylist.copy(id = id)
        }

        val gson = Gson()
        val parsedItems = gson.fromJson(buylist.itemsJson, Array<ChecklistItem>::class.java)
        Checklist(
            id = Id(buylist.id),
            name = buylist.name,
            items = parsedItems?.toList().orEmpty()
        )
    }

    override fun getChecklists(): Flow<List<Checklist>> {

        return checklistDao.getChecklists().map {
            it.map { entity ->
                val gson = Gson()
                val items =
                    gson.fromJson(entity.itemsJson, Array<ChecklistItem>::class.java).toList()
                Checklist(
                    id = Id(entity.id),
                    name = entity.name,
                    items = items
                )
            }
        }
    }

    override suspend fun updateChecklist(newValue: Checklist) = withContext(Dispatchers.IO) {
        val gson = Gson()
        checklistDao.updateChecklist(
            ChecklistEntity(
                id = newValue.id.id,
                name = newValue.name,
                itemsJson = gson.toJson(newValue.items)
            )
        )
    }

    private fun getNewGlobalId(): Long {
        return maxOf(
            inboxTaskDao.getMaxId(),
            noteDao.getMaxId(),
            checklistDao.getMaxId()
        ) + 1
    }

    override suspend fun deleteById(id: Id) = withContext(Dispatchers.IO) {
        inboxTaskDao.deleteInboxTask(id.id)
        noteDao.deleteNote(id.id)
        checklistDao.deleteChecklist(id.id)
    }
}
