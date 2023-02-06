package by.kimentiy.notes

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class NoteEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String
)

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addNote(note: NoteEntity): Long

    @Query("SELECT MAX(id) FROM NoteEntity")
    fun getMaxId(): Long

    @Query("SELECT * FROM NoteEntity")
    fun getNotes(): Flow<List<NoteEntity>>

    @Update
    fun updateNote(note: NoteEntity)

    @Query("DELETE FROM NoteEntity WHERE id=:id")
    fun deleteNote(id: Long)
}
