package by.kimentiy.notes.models

import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.repositories.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ChecklistsViewModel(
    private val scope: CoroutineScope,
    private val repository: NotesRepository
) {

    private val _checklists = MutableStateFlow<List<ChecklistViewModel>>(emptyList())
    val checklists: StateFlow<List<ChecklistViewModel>> = _checklists

    init {
        scope.launch {
            repository.getChecklists()
                .map {
                    it.map { checklist -> ChecklistViewModel(checklist, scope, repository) }
                }.collect {
                    _checklists.value = it
                }
        }
    }

    fun getChecklistById(id: Id): ChecklistViewModel {
        return _checklists.value.find { it.id == id } ?: ChecklistViewModel(null, scope, repository)
    }
}
