package by.kimentiy.notes.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class InboxTaskEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo val title: String,
    @ColumnInfo val description: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean,
    @ColumnInfo val subtasksJson: String
)

@Dao
interface InboxTaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addInboxTask(task: InboxTaskEntity): Long

    @Query("SELECT MAX(id) FROM InboxTaskEntity")
    fun getMaxId(): Long

    @Query("SELECT * FROM InboxTaskEntity")
    fun getInboxTasks(): Flow<List<InboxTaskEntity>>

    @Update
    fun updateInboxTask(task: InboxTaskEntity)

    @Query("DELETE FROM InboxTaskEntity WHERE id=:id")
    fun deleteInboxTask(id: Long)
}
