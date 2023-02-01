package by.kimentiy.notes.models

import by.kimentiy.notes.repositories.NotesRepository
import kotlinx.coroutines.CoroutineScope

class ChecklistsViewModel(
    private val scope: CoroutineScope,
    private val repository: NotesRepository
) {

    private lateinit var buylistModel: ChecklistViewModel

    suspend fun getBuylist(): ChecklistViewModel {
        if (!::buylistModel.isInitialized) {
            buylistModel = ChecklistViewModel(
                checklist = repository.getBuylist(),
                scope = scope,
                repository = repository
            )
        }
        return buylistModel
    }
}
