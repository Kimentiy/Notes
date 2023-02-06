package by.kimentiy.notes

import by.kimentiy.notes.repositories.*
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class SqlDelightNotesRepository(
    driverFactory: SqlDelightDriverFactory,
    scope: CoroutineScope
) : NotesRepository {

    private val gson = Gson()
    private val driver = driverFactory.createDriver()
    private val database = Database(driver)

    private var notes = MutableStateFlow<List<Note>>(emptyList())
    private var checklists = MutableStateFlow<List<Checklist>>(emptyList())
    private var _inboxTasks = MutableStateFlow<List<InboxTask>>(emptyList())

    init {
        scope.launch(Dispatchers.IO) {
            val buylist = database.databaseQueries.getAllChecklists().executeAsOneOrNull()
            if (buylist == null) {
                val freshBuylist = Checklist(
                    id = Id(1),
                    name = "Buylist",
                    items = emptyList(),
                    scn = 0,
                    lastModified = Clock.System.now()
                )
                database.databaseQueries.insertChecklist(
                    id = freshBuylist.id.id,
                    name = freshBuylist.name,
                    itemsJson = "",
                    scn = freshBuylist.scn,
                    lastModifiedTimestamp = freshBuylist.lastModified.toDbTime()
                )

                checklists.value = listOf(freshBuylist)
            } else {
                checklists.value = listOf(
                    Checklist(
                        id = Id(buylist.id),
                        name = buylist.name,
                        items = gson.fromJson(buylist.itemsJson, Array<ChecklistItem>::class.java)
                            ?.toList().orEmpty(),
                        scn = buylist.scn,
                        lastModified = Instant.fromEpochMilliseconds(buylist.lastModifiedTimestamp)
                    )
                )
            }

            notes.value = database.databaseQueries.getAllNotes().executeAsList().map {
                Note(
                    id = Id(it.id),
                    title = it.title,
                    description = it.description,
                    scn = it.scn,
                    lastModified = Instant.fromEpochMilliseconds(it.lastModifiedTimestamp)
                )
            }

            _inboxTasks.value = database.databaseQueries.getAllInboxTasks().executeAsList().map {
                InboxTask(
                    id = Id(it.id),
                    title = it.title,
                    description = it.description,
                    isCompleted = it.isCompleted,
                    subtasks = gson.fromJson(it.subtasksJson, Array<Subtask>::class.java)?.toList()
                        .orEmpty(),
                    scn = it.scn,
                    lastModified = it.lastModifiedTimestamp.toDomainTime()
                )
            }
        }
    }

    override val inboxTasks: Flow<List<InboxTask>>
        get() = _inboxTasks

    override suspend fun updateInboxTask(newValue: InboxTask) = withContext(Dispatchers.IO) {
        database.databaseQueries.updateInboxTask(
            id = newValue.id.id,
            title = newValue.title,
            description = newValue.description,
            isCompleted = newValue.isCompleted,
            subtasksJson = gson.toJson(newValue.subtasks)
        )

        _inboxTasks.updateValue(newValue)
    }

    override suspend fun createInboxTask(
        title: String,
        description: String,
        subtasks: List<Subtask>
    ): InboxTask = withContext(Dispatchers.IO) {
        val inboxTask = InboxTask(
            id = Id(getNewGlobalId()),
            title = title,
            description = description,
            isCompleted = false,
            subtasks = subtasks,
            scn = 0,
            lastModified = Clock.System.now()
        )

        database.databaseQueries.insertInboxTask(
            id = inboxTask.id.id,
            title = title,
            description = description,
            isCompleted = inboxTask.isCompleted,
            subtasksJson = gson.toJson(subtasks),
            scn = inboxTask.scn,
            lastModifiedTimestamp = inboxTask.lastModified.toDbTime()
        )

        _inboxTasks.addValue(inboxTask)

        inboxTask
    }

    override suspend fun getNotes(): Flow<List<Note>> {
        return notes
    }

    override suspend fun createNote(title: String, description: String): Note =
        withContext(Dispatchers.IO) {
            val note = Note(
                id = Id(getNewGlobalId()),
                title = title,
                description = description,
                scn = 0,
                lastModified = Clock.System.now()
            )
            database.databaseQueries.insertNote(
                id = note.id.id,
                title = title,
                description = description,
                scn = note.scn,
                lastModifiedTimestamp = note.lastModified.toDbTime()
            )

            notes.addValue(note)

            note
        }

    override suspend fun updateNote(newValue: Note) = withContext(Dispatchers.IO) {
        database.databaseQueries.updateNote(
            id = newValue.id.id,
            title = newValue.title,
            description = newValue.description
        )

        notes.updateValue(newValue)
    }

    override fun getChecklists(): Flow<List<Checklist>> {
        return checklists
    }

    override suspend fun updateChecklist(newValue: Checklist) {
        database.databaseQueries.updateChecklist(
            id = newValue.id.id,
            name = newValue.name,
            itemsJson = gson.toJson(newValue.items)
        )

        checklists.updateValue(newValue)
    }

    override suspend fun createChecklist(name: String, items: List<ChecklistItem>): Checklist =
        withContext(Dispatchers.IO) {
            val checklist = Checklist(
                id = Id(getNewGlobalId()),
                name = name,
                items = items,
                scn = 0,
                lastModified = Clock.System.now()
            )

            database.databaseQueries.insertChecklist(
                id = checklist.id.id,
                name = checklist.name,
                itemsJson = gson.toJson(checklist.items),
                scn = checklist.scn,
                lastModifiedTimestamp = checklist.lastModified.toDbTime()
            )

            checklists.addValue(checklist)

            checklist
        }

    override suspend fun deleteById(id: Id) = withContext(Dispatchers.IO) {
        database.databaseQueries.deleteNote(id.id)
        database.databaseQueries.deleteChecklist(id.id)
        database.databaseQueries.deleteInboxTask(id.id)

        notes.removeValue(id)
        checklists.removeValue(id)
        _inboxTasks.removeValue(id)
    }

    private fun getNewGlobalId(): Long {
        return maxOf(
            database.databaseQueries.getMaxNoteId().executeAsOne().MAX ?: 0,
            database.databaseQueries.getMaxInboxTaskId().executeAsOne().MAX ?: 0,
            database.databaseQueries.getMaxChecklistId().executeAsOne().MAX ?: 0
        ) + 1
    }

    private fun <T : WithGlobalId> MutableStateFlow<List<T>>.addValue(item: T) {
        value = value + listOf(item)
    }

    private fun <T : WithGlobalId> MutableStateFlow<List<T>>.updateValue(newValue: T) {
        value = value.map { element ->
            newValue.takeIf { it.id == element.id } ?: element
        }
    }

    private fun <T : WithGlobalId> MutableStateFlow<List<T>>.removeValue(id: Id) {
        value = value.filterNot { it.id == id }
    }

    private fun Instant.toDbTime(): Long {
        return toEpochMilliseconds()
    }

    private fun Long.toDomainTime(): Instant {
        return Instant.fromEpochMilliseconds(this)
    }
}