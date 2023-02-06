package by.kimentiy.notes.models

import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.repositories.InboxTask
import by.kimentiy.notes.repositories.NotesRepository
import by.kimentiy.notes.repositories.Subtask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InboxTaskViewModel(
    private val scope: CoroutineScope,
    private val repository: NotesRepository,
    private val inboxTask: InboxTask? = null
) {

    val id: Id? = inboxTask?.id

    private val _title = MutableStateFlow(inboxTask?.title.orEmpty())
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow(inboxTask?.description.orEmpty())
    val description: StateFlow<String> = _description

    private val _subtasks =
        MutableStateFlow(inboxTask?.subtasks?.mapToModels().orEmpty())
    val subtasks: StateFlow<List<SubtaskViewModel>> = _subtasks

    private val _isCompleted = MutableStateFlow(inboxTask?.isCompleted ?: false)
    val isCompleted: StateFlow<Boolean> = _isCompleted

    fun setTitle(title: String) {
        _title.value = title
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun setIsCompleted(isCompleted: Boolean) {
        _isCompleted.value = isCompleted
    }

    fun onApplySubtaskClicked(subtaskModel: SubtaskViewModel) {
        val subtask = Subtask(
            title = subtaskModel.title.value,
            description = subtaskModel.description.value,
            isCompleted = subtaskModel.isCompleted.value
        )
        if (subtaskModel.subtask == null) {
            _subtasks.value = _subtasks.value + SubtaskViewModel(subtask)
        } else {
            _subtasks.value = _subtasks.value.map {
                if (it.subtask == subtaskModel.subtask) {
                    SubtaskViewModel(subtask)
                } else {
                    it
                }
            }
        }
    }

    fun saveData() {
        if (inboxTask == null) {
            scope.launch {
                repository.createInboxTask(
                    title = title.value,
                    description = description.value,
                    subtasks = subtasks.value.mapToSubtasks()
                )
            }
        } else {
            scope.launch {
                repository.updateInboxTask(
                    inboxTask.copy(
                        title = title.value,
                        description = description.value,
                        isCompleted = isCompleted.value,
                        subtasks = subtasks.value.mapToSubtasks()
                    )
                )
            }
        }
    }

    private fun List<SubtaskViewModel>.mapToSubtasks(): List<Subtask> {
        return map {
            Subtask(
                title = it.title.value,
                description = it.description.value,
                isCompleted = it.isCompleted.value
            )
        }
    }

    private fun List<Subtask>.mapToModels(): List<SubtaskViewModel> {
        return map {
            SubtaskViewModel(it)
        }
    }
}

class SubtaskViewModel(
    val subtask: Subtask? = null
) {
    private val initialTitle = subtask?.title.orEmpty()
    private val initialDescription = subtask?.description.orEmpty()
    private val initialIsCompleted = subtask?.isCompleted ?: false

    val title = MutableStateFlow(initialTitle)
    val description = MutableStateFlow(initialDescription)
    val isCompleted = MutableStateFlow(initialIsCompleted)

    fun reset() {
        title.value = initialTitle
        description.value = initialDescription
        isCompleted.value = initialIsCompleted
    }
}
