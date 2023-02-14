package by.kimentiy.notes.michsync

import by.kimentiy.notes.network.httpClient
import by.kimentiy.notes.repositories.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.toJavaInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class SyncMichRepository(
    private val settingsRepository: SettingsRepository,
    private val notesRepository: NotesRepository
) : SyncRepository {

    private val address = Url("https://mmich.online:3000/fullsync")
    private val client = httpClient()
    private val jsonSerializer = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    override suspend fun syncNotes(): Result<Unit> = withContext(Dispatchers.IO) {
        val lastSyncTime = settingsRepository.lastSyncTime
        val notes = notesRepository.getNotes().first()
        val remoteNotes =
            if (lastSyncTime == null) {
                notes.map {
                    it.toRemoteNote(action = Action.CREATE)
                }
            } else {
                val changedRemoteNotes =
                    notes
                        .filter { it.lastModified > lastSyncTime }
                        .map {
                            it.toRemoteNote(
                                action = if (it.scn == 0L) {
                                    Action.CREATE
                                } else {
                                    Action.UPDATE
                                }
                            )
                        }
                val deletedNotes = notesRepository.getAllDeletedNotes().map {
                    RemoteNote(
                        id = it.id.id,
                        scn = it.scn,
                        modifiedAt = it.lastModified.format(),
                        name = "",
                        text = "",
                        action = Action.DELETE.name,
                    )
                }

                changedRemoteNotes + deletedNotes
            }

        val request = MichRequest(
            lastSyncTime = lastSyncTime?.format(),
            news = remoteNotes
        )
        val requestJson = jsonSerializer.encodeToString(request)
        println("Request: $requestJson")
        val serverResponse = client.post {
            contentType(ContentType.Application.Json)
            setBody(requestJson)
            url(address.toString())
        }

        if (serverResponse.status.isSuccess()) {
            val json = serverResponse.bodyAsText()

            print("Response:")
            printJson(json)

            val response = jsonSerializer.decodeFromString<MichResponse>(json)

            handleResponse(notes, response)

            Result.success(Unit)
        } else {
            Result.failure(Exception("Server returned bad status: ${serverResponse.status}"))
        }
    }

    private suspend fun handleResponse(notes: List<Note>, response: MichResponse) {
        response.news.forEach {
            when (it.action.lowercase()) {
                Action.CREATE.name.lowercase() -> {
                    notesRepository.createNote(
                        id = it.id,
                        title = requireNotNull(it.name),
                        description = requireNotNull(it.text),
                        scn = it.scn,
                        lastModified = parseTimestamp(it.modifiedAt)
                    )
                }
                Action.UPDATE.name.lowercase() -> {
                    notesRepository.updateNote(
                        id = Id(requireNotNull(it.id)),
                        title = requireNotNull(it.name),
                        description = requireNotNull(it.text),
                        scn = it.scn,
                        lastModified = parseTimestamp(it.modifiedAt)
                    )
                }
                Action.DELETE.name.lowercase() -> {
                    notesRepository.deleteById(Id(requireNotNull(it.id)))
                }
            }
        }
        response.conflicts.forEach { remoteNote ->
            val localNote = notes.find { it.id.id == remoteNote.id }
            val remoteNoteId = Id(requireNotNull(remoteNote.id))

            val conflict = ConflictNote(
                id = remoteNoteId,
                serverVersion = if (!remoteNote.deleted) {
                    NoteVersion.Version(
                        title = remoteNote.name ?: "",
                        description = remoteNote.text ?: ""
                    )
                } else {
                    NoteVersion.Deleted
                },
                localVersion = localNote?.let {
                    NoteVersion.Version(
                        title = it.title,
                        description = it.description
                    )
                } ?: NoteVersion.Deleted,
                resultScn = remoteNote.scn
            )

            settingsRepository.addConflictNote(conflict)

            notesRepository.deleteById(remoteNoteId)
        }
        response.confirms.forEach {
            notesRepository.updateNote(
                id = it.clientId ?: it.id,
                newId = it.id,
                scn = it.scn
            )
        }

        notesRepository.clearDeletedNotes()

        settingsRepository.lastSyncTime = parseTimestamp(response.lastSyncTime)
    }

    private fun Note.toRemoteNote(action: Action): RemoteNote {
        return RemoteNote(
            id = id.id.takeUnless { action == Action.CREATE },
            clientId = id.id.takeIf { action == Action.CREATE },
            name = title,
            text = description,
            scn = scn,
            action = action.name,
            modifiedAt = lastModified.format()
        )
    }

    private fun parseTimestamp(str: String?): Instant? {
        return str
            ?.let { ZonedDateTime.parse(it, dateTimeFormatter) }
            ?.toEpochSecond()
            ?.let { Instant.fromEpochSeconds(it) }
    }
    private fun Instant.format(): String {
        return ZonedDateTime.ofInstant(
            this.toJavaInstant(), ZoneOffset.UTC
        ).format(dateTimeFormatter)
    }

    private fun printJson(json: String) {
        println(jsonSerializer.encodeToString(jsonSerializer.parseToJsonElement(json)))
    }

    companion object {
        private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")
    }

    enum class Action {
        CREATE, UPDATE, DELETE
    }
}

@Serializable
data class MichRequest(
    val lastSyncTime: String?, val news: List<RemoteNote>
)

@Serializable
data class MichResponse(
    val lastSyncTime: String,
    val news: List<RemoteNote>,
    val confirms: List<RemoteConfirm>,
    val conflicts: List<RemoteNote>
)

@Serializable
data class RemoteNote(
    val id: Long? = null,
    val clientId: Long? = null,
    val name: String?,
    val text: String?,
    val scn: Long,
    val action: String,
    val deleted: Boolean = false,
    val modifiedAt: String?
)

@Serializable
data class RemoteConfirm(
    val id: Long,
    val scn: Long,
    val clientId: Long? = null,
    val deleted: Boolean? = false,
)
