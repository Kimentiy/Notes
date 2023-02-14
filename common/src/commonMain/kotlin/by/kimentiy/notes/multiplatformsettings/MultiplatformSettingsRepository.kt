package by.kimentiy.notes.multiplatformsettings

import by.kimentiy.notes.repositories.ConflictNote
import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.repositories.SettingsRepository
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MultiplatformSettingsRepository : SettingsRepository {

    private val settings = Settings()

    override var lastSyncTime: Instant? = null
        get() {
            return settings.getLongOrNull(KEY_LAST_SYNC_TIME)?.let {
                Instant.fromEpochMilliseconds(it)
            }
        }
        set(value) {
            settings[KEY_LAST_SYNC_TIME] = value?.toEpochMilliseconds()

            field = value
        }

    override fun addConflictNote(note: ConflictNote) {
        settings[KEY_CONFLICT_NOTES] = Json.encodeToString(getAllConflicts() + listOf(note))
    }

    override fun getAllConflicts(): List<ConflictNote> {
        return settings.getStringOrNull(KEY_CONFLICT_NOTES)
            ?.let { Json.decodeFromString<List<ConflictNote>>(it) }
            ?.toList()
            ?: emptyList()
    }

    override fun removeConflictNote(id: Id) {
        settings[KEY_CONFLICT_NOTES] = getAllConflicts().filter {
            it.id != id
        }
    }

    companion object {
        private const val KEY_LAST_SYNC_TIME = "KEY_LAST_SYNC_TIME"
        private const val KEY_CONFLICT_NOTES = "KEY_CONFLICT_NOTES"
    }
}
