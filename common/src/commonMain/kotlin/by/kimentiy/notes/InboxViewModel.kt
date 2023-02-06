package by.kimentiy.notes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class InboxViewModel(
    private val scope: CoroutineScope,
    private val repository: NotesRepository
) {

    private val _tasks = MutableStateFlow<List<InboxTaskViewModel>>(emptyList())
    val tasks: StateFlow<List<InboxTaskViewModel>> = _tasks

    init {
        scope.launch {
            repository.inboxTasks.collect {
                _tasks.value = it.toModels()
            }
        }
    }

    suspend fun getTaskById(id: Id?): InboxTaskViewModel {
        return tasks.value.find { it.id == id } ?: repository.createInboxTask(
            title = "",
            description = "",
            subtasks = emptyList()
        ).toModel()
    }

    fun saveData() {
        tasks.value.forEach { it.saveData() }
    }

    private fun List<InboxTask>.toModels(): List<InboxTaskViewModel> {
        return map {
            it.toModel()
        }
    }

    private fun InboxTask.toModel(): InboxTaskViewModel {
        return InboxTaskViewModel(
            scope = scope,
            repository = repository,
            inboxTask = this
        )
    }
}
