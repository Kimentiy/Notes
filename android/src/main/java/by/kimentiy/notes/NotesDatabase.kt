package by.kimentiy.notes

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [InboxTaskEntity::class, NoteEntity::class, ChecklistEntity::class],
    version = 1
)
abstract class NotesDatabase : RoomDatabase() {

    abstract fun inboxTaskDao(): InboxTaskDao

    abstract fun noteDao(): NoteDao

    abstract fun checklistDao(): ChecklistDao
}
