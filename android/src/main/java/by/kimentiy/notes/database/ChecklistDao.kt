package by.kimentiy.notes.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class ChecklistEntity(
    @PrimaryKey val id: Long,
    @ColumnInfo val name: String,
    @ColumnInfo val itemsJson: String
)

@Dao
interface ChecklistDao {

    @Query("SELECT MAX(id) FROM ChecklistEntity")
    fun getMaxId(): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addChecklist(checklist: ChecklistEntity): Long

    @Query("SELECT * FROM ChecklistEntity")
    fun getChecklists(): Flow<List<ChecklistEntity>>

    @Update
    fun updateChecklist(checklist: ChecklistEntity)

    @Query("DELETE FROM ChecklistEntity WHERE id=:id")
    fun deleteChecklist(id: Long)
}
