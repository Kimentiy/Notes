package by.kimentiy.notes

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChecklistViewModel(
    private val checklist: Checklist,
    private val scope: CoroutineScope,
    private val repository: NotesRepository
) {

    val id: Id = checklist.id

    private val _name = MutableStateFlow(checklist.name)
    val name: StateFlow<String> = _name

    private val _items = MutableStateFlow(checklist.items.map(::ChecklistItemViewModel))
    val items: StateFlow<List<ChecklistItemViewModel>> = _items

    fun setName(name: String) {
        _name.value = name
    }

    fun addItem(title: String) {
        _items.value = items.value + ChecklistItemViewModel(ChecklistItem(title, isChecked = false))
    }

    fun saveChanges() {
        scope.launch {
            repository.updateChecklist(
                checklist.copy(
                    name = name.value,
                    items = items.value.map {
                        ChecklistItem(
                            title = it.title.value,
                            isChecked = it.isChecked.value
                        )
                    }
                )
            )
        }
    }
}

class ChecklistItemViewModel(private val item: ChecklistItem) {

    private val _title = MutableStateFlow(item.title)
    val title: StateFlow<String> = _title

    private val _isChecked = MutableStateFlow(item.isChecked)
    val isChecked: StateFlow<Boolean> = _isChecked

    fun setTitle(title: String) {
        _title.value = title
    }

    fun resetTitle() {
        _title.value = item.title
    }

    fun setIsChecked(value: Boolean) {
        _isChecked.value = value
    }
}
