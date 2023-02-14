package by.kimentiy.notes

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.room.Room
import by.kimentiy.notes.database.NotesDatabase
import by.kimentiy.notes.repositories.NotesRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking

class NotesContentProvider : ContentProvider() {

    private lateinit var repository: NotesRepository

    override fun onCreate(): Boolean {
        repository = SqlDelightNotesRepository(
            driverFactory = SqlDelightDriverFactory(context!!),
            scope = GlobalScope
        )
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
//        Log.d("MyTag", "Thread: ${Thread.currentThread()}")
//        Thread.sleep(3000)
//        return runBlocking {
//            values?.let {
//                val note = repository.createNote(
//                    title = values.getAsString("title"),
//                    description = values.getAsString("description")
//                )
//                Uri.withAppendedPath(uri, note.id.id.toString())
//            }
//        }
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("Not yet implemented")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }
}
