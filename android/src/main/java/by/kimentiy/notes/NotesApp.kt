package by.kimentiy.notes

import android.app.Application
import android.content.Context
import by.kimentiy.notes.michsync.SyncMichRepository
import by.kimentiy.notes.multiplatformsettings.MultiplatformSettingsRepository
import by.kimentiy.notes.repositories.NotesRepository
import by.kimentiy.notes.repositories.SettingsRepository
import by.kimentiy.notes.repositories.SyncRepository
import kotlinx.coroutines.GlobalScope

class NotesApp : Application() {

    lateinit var repository: NotesRepository
    lateinit var settingsRepository: SettingsRepository
    lateinit var syncRepository: SyncRepository

    override fun onCreate() {
        super.onCreate()

        repository = SqlDelightNotesRepository(
            driverFactory = SqlDelightDriverFactory(this),
            scope = GlobalScope
        )
        settingsRepository = MultiplatformSettingsRepository()
        syncRepository = SyncMichRepository(
            settingsRepository = settingsRepository,
            notesRepository = repository
        )
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}
