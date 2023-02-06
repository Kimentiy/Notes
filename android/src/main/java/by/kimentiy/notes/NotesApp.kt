package by.kimentiy.notes

import android.app.Application
import android.content.Context
import androidx.room.Room
import by.kimentiy.notes.database.NotesDatabase
import by.kimentiy.notes.repositories.NotesRepository
import by.kimentiy.notes.repositories.SyncRepository
import kotlinx.coroutines.GlobalScope

class NotesApp : Application() {

    lateinit var repository: NotesRepository
    lateinit var syncRepository: SyncRepository

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            NotesDatabase::class.java,
            "notes-database"
        ).build()
        repository = SqlDelightNotesRepository(
            driverFactory = SqlDelightDriverFactory(this),
            scope = GlobalScope
        )
        syncRepository = SyncHttpRepository()
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}
