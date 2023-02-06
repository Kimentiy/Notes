package by.kimentiy.notes.ui.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import by.kimentiy.notes.*
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    inboxViewModel: InboxViewModel,
    checklistsViewModel: ChecklistsViewModel,
    notesViewModel: NotesViewModel,
    repository: NotesRepository,
    onInboxClicked: (InboxViewModel) -> Unit,
    onChecklistClicked: (ChecklistViewModel) -> Unit,
    onSearchClicked: () -> Unit,
    navigateToEditScreen: (Id?) -> Unit,
    navigateToAddPurchaseScreen: () -> Unit,
    onAddInboxClicked: () -> Unit
) {
    val selectedNotes = remember { mutableStateOf(emptyList<Id>()) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background,
        topBar = {
            if (selectedNotes.value.isNotEmpty()) {
                SelectionTopBar(
                    selectedCount = selectedNotes.value.size,
                    onCancelClicked = {
                        selectedNotes.value = emptyList()
                    }
                )
            } else {
                NotesTopBar()
            }
        },
        bottomBar = {
            if (selectedNotes.value.isNotEmpty()) {
                SelectionBottomBar(
                    onDeleteClicked = {
                        coroutineScope.launch {
                            selectedNotes.value.forEach {
                                repository.deleteById(it)
                            }
                            selectedNotes.value = emptyList()
                        }

                    }
                )
            } else {
                NotesBottomBar(
                    onSearchClicked = onSearchClicked,
                    onAddNoteClicked = {
                        navigateToEditScreen(null)
                    },
                    onAddPurchaseClicked = navigateToAddPurchaseScreen,
                    onAddInboxClicked = onAddInboxClicked
                )
            }
        }
    ) {
        NotesGrid(
            paddingValues = it,
            inboxViewModel = inboxViewModel,
            checklistsViewModel = checklistsViewModel,
            notesViewModel = notesViewModel,
            selectedNotes = selectedNotes,
            onInboxClicked = onInboxClicked,
            onChecklistClicked = onChecklistClicked,
            onNoteClicked = { id ->
                navigateToEditScreen(id)
            })
    }
}

@Composable
fun NotesGrid(
    paddingValues: PaddingValues,
    inboxViewModel: InboxViewModel,
    checklistsViewModel: ChecklistsViewModel,
    notesViewModel: NotesViewModel,
    selectedNotes: MutableState<List<Id>>,
    onInboxClicked: (InboxViewModel) -> Unit,
    onChecklistClicked: (ChecklistViewModel) -> Unit,
    onNoteClicked: (Id) -> Unit
) {
    val notes = notesViewModel.notes.collectAsState().value
    val buylist = remember { mutableStateOf<ChecklistViewModel?>(null) }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        item {
            val content =
                inboxViewModel.tasks.collectAsState().value.joinToString(separator = "\n") {
                    it.title.value
                }

            Note(
                title = "Inbox",
                content = content,
                isSelected = false,
                onClick = {
                    if (selectedNotes.value.isEmpty()) {
                        onInboxClicked(inboxViewModel)
                    }
                },
                onLongClick = {
                    // do nothing
                }
            )
        }

        if (buylist.value != null) {
            item {
                val buylistId = buylist.value!!.id
                val content =
                    buylist.value!!.items.collectAsState().value.joinToString(separator = "\n") {
                        it.title.value
                    }

                Note(
                    title = buylist.value!!.name.collectAsState().value,
                    content = content,
                    isSelected = buylistId in selectedNotes.value,
                    onClick = {
                        if (selectedNotes.value.isEmpty()) {
                            onChecklistClicked(buylist.value!!)
                        }
                        // General delete logic for usual checklist
//                        if (selectedNotes.value.isEmpty()) {
//                            onChecklistClicked(buylist.value!!)
//                        } else if (selectedNotes.value.contains(buylistId)) {
//                            selectedNotes.value = selectedNotes.value.filterNot { it == buylistId }
//                        } else {
//                            selectedNotes.value = selectedNotes.value + listOf(buylistId)
//                        }
                    },
                    onLongClick = {
                        // Buylist can't be deleted
                        // selectedNotes.value = selectedNotes.value + listOf(buylistId)
                    }
                )
            }
        }

        items(items = notes) { note ->
            Note(
                title = note.title.collectAsState().value,
                content = note.description.collectAsState().value,
                isSelected = note.id in selectedNotes.value,
                onClick = {
                    if (selectedNotes.value.isEmpty()) {
                        onNoteClicked(note.id!!)
                    } else if (selectedNotes.value.contains(note.id)) {
                        selectedNotes.value = selectedNotes.value.filterNot { it == note.id }
                    } else {
                        selectedNotes.value = selectedNotes.value + listOfNotNull(note.id)
                    }
                },
                onLongClick = {
                    selectedNotes.value = selectedNotes.value + listOfNotNull(note.id)
                }
            )
        }
    }
    LaunchedEffect(null) {
        buylist.value = checklistsViewModel.getBuylist()
    }
}
