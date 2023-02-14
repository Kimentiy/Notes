package by.kimentiy.notes

import by.kimentiy.notes.repositories.*
import by.kimentiy.notes.repositories.DeletedNote
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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
            checklists.value = database.databaseQueries.getAllChecklists().executeAsList().map {
                Checklist(
                    id = Id(it.id),
                    name = it.name,
                    items = gson.fromJson(it.itemsJson, Array<ChecklistItem>::class.java)
                        ?.toList().orEmpty(),
                    scn = it.scn,
                    lastModified = Instant.fromEpochMilliseconds(it.lastModifiedTimestamp)
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
            id = getNewId(),
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

    override suspend fun createNote(
        id: Long?,
        title: String,
        description: String,
        scn: Long,
        lastModified: Instant?
    ): Note =
        withContext(Dispatchers.IO) {
            val note = Note(
                id = id?.let { Id(it) } ?: getNewId(),
                title = title,
                description = description,
                scn = scn,
                lastModified = lastModified ?: Clock.System.now()
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

    override suspend fun updateNote(
        id: Id,
        title: String,
        description: String,
        scn: Long,
        lastModified: Instant?
    ) = withContext(Dispatchers.IO) {
        val newNote = Note(
            id = id,
            title = title,
            description = description,
            scn = scn,
            lastModified = lastModified ?: Clock.System.now()
        )

        database.databaseQueries.updateNote(
            id = newNote.id.id,
            title = newNote.title,
            description = newNote.description,
            scn = newNote.scn,
            lastModifiedTimestamp = newNote.lastModified.toDbTime()
        )

        notes.updateValue(newNote)
    }

    override suspend fun updateNote(id: Long, newId: Long?, scn: Long) {
        database.databaseQueries.updateNoteIdAndScn(
            id = id,
            scn = scn,
            id_ = newId ?: id
        )

        notes.value = notes.value.map {
            if (it.id.id == id) {
                it.copy(
                    id = Id(newId ?: id),
                    scn = scn
                )
            } else {
                it
            }
        }
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
                id = getNewId(),
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
        val note = notes.value.find { it.id == id }
        val checklist = checklists.value.find { it.id == id }
        val inboxTask = checklists.value.find { it.id == id }

        if (note != null) {
            database.databaseQueries.deleteNote(id.id)
            insertDeletedNote(note)

            notes.removeValue(id)
        } else if (checklist != null) {
            database.databaseQueries.deleteChecklist(id.id)
            insertDeletedNote(checklist)

            checklists.removeValue(id)
        } else if (inboxTask != null) {
            database.databaseQueries.deleteInboxTask(id.id)
            insertDeletedNote(inboxTask)

            _inboxTasks.removeValue(id)
        }
    }

    override suspend fun getAllDeletedNotes(): List<DeletedNote> = withContext(Dispatchers.IO) {
        database.databaseQueries.getAllDeletedNotes().executeAsList().map {
            DeletedNote(
                id = Id(it.id),
                scn = it.scn,
                lastModified = it.lastModifiedTimestamp.toDomainTime()
            )
        }
    }

    override suspend fun clearDeletedNotes() = withContext(Dispatchers.IO) {
        database.databaseQueries.clearDeletedNotes()
    }

    private fun getNewId(): Id {
        return Id(Clock.System.now().toEpochMilliseconds())
    }

    private fun <T : Notelike> MutableStateFlow<List<T>>.addValue(item: T) {
        value = value + listOf(item)
    }

    private fun <T : Notelike> MutableStateFlow<List<T>>.updateValue(newValue: T) {
        value = value.map { element ->
            newValue.takeIf { it.id == element.id } ?: element
        }
    }

    private fun <T : Notelike> MutableStateFlow<List<T>>.removeValue(id: Id) {
        value = value.filterNot { it.id == id }
    }

    private fun <T : Notelike> insertDeletedNote(value: T) {
        database.databaseQueries.insertDeletedNote(
            id = value.id.id,
            scn = value.scn,
            lastModifiedTimestamp = value.lastModified.toDbTime()
        )
    }

    private fun Instant.toDbTime(): Long {
        return toEpochMilliseconds()
    }

    private fun Long.toDomainTime(): Instant {
        return Instant.fromEpochMilliseconds(this)
    }
}