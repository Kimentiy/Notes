package by.kimentiy.notes.models

import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.repositories.Note
import by.kimentiy.notes.repositories.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NotesViewModel(
    private val scope: CoroutineScope,
    private val repository: NotesRepository
) {

    private val _notes = MutableStateFlow<List<NoteViewModel>>(emptyList())
    val notes: StateFlow<List<NoteViewModel>> = _notes

    init {
        scope.launch {
            repository.getNotes()
                .map {
                    it.map { note -> NoteViewModel(note, scope, repository) }
                }.collect {
                    _notes.value = it
                }
        }
    }

    fun getNoteById(id: Id): NoteViewModel {
        return notes.value.find { it.id == id } ?: NoteViewModel(null, scope, repository)
    }
}

class NoteViewModel(
    private val note: Note?,
    private val scope: CoroutineScope,
    private val repository: NotesRepository
) {

    val id: Id? = note?.id

    private val _title = MutableStateFlow(note?.title.orEmpty())
    val title: StateFlow<String> = _title

    private val _description = MutableStateFlow(note?.description.orEmpty())
    val description: StateFlow<String> = _description

    fun setTitle(value: String) {
        _title.value = value
    }

    fun setDescription(description: String) {
        _description.value = description
    }

    fun saveChanges() {
        val currentNote = note

        scope.launch {
            if (currentNote != null) {
                repository.updateNote(
                    id = currentNote.id,
                    title = title.value,
                    description = description.value,
                    scn = currentNote.scn
                )
            } else if (title.value.isNotBlank() || description.value.isNotBlank()) {
                repository.createNote(
                    title = title.value,
                    description = description.value
                )
            }
        }
    }

    fun reset() {
        _title.value = note?.title.orEmpty()
        _description.value = note?.description.orEmpty()
    }
}
