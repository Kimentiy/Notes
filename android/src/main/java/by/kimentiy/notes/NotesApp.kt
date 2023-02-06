package by.kimentiy.notes

import android.app.Application
import android.content.Context
import androidx.room.Room

class NotesApp : Application() {

    lateinit var repository: NotesRepository

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            NotesDatabase::class.java,
            "notes-database"
        ).build()
        repository = RoomNotesRepository(
            inboxTaskDao = database.inboxTaskDao(),
            noteDao = database.noteDao(),
            checklistDao = database.checklistDao()
        )
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }
}
