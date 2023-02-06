package by.kimentiy.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope

class NotesViewModelFactory(private val repository: NotesRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(InboxViewModelWrapper::class.java) -> {
                InboxViewModelWrapper(repository) as T
            }
            modelClass.isAssignableFrom(ChecklistsViewModelWrapper::class.java) -> {
                ChecklistsViewModelWrapper(repository) as T
            }
            modelClass.isAssignableFrom(NotesViewModelWrapper::class.java) -> {
                NotesViewModelWrapper(repository) as T
            }
            else -> super.create(modelClass)
        }
    }
}

class InboxViewModelWrapper(repository: NotesRepository) : ViewModel() {

    val model = InboxViewModel(viewModelScope, repository)
}

class ChecklistsViewModelWrapper(repository: NotesRepository) : ViewModel() {

    val model = ChecklistsViewModel(viewModelScope, repository)
}

class NotesViewModelWrapper(repository: NotesRepository) : ViewModel() {

    val model = NotesViewModel(viewModelScope, repository)
}
