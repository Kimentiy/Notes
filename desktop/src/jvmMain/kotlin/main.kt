import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import by.kimentiy.notes.AddPurchaseDialog
import by.kimentiy.notes.EditSubtaskDialog
import by.kimentiy.notes.SqlDelightDriverFactory
import by.kimentiy.notes.SqlDelightNotesRepository
import by.kimentiy.notes.models.*
import by.kimentiy.notes.repositories.NotesRepository
import by.kimentiy.notes.ui.EditInboxItemScreen
import by.kimentiy.notes.ui.EditNoteScreen
import by.kimentiy.notes.ui.InboxScreen
import by.kimentiy.notes.ui.Screens
import by.kimentiy.notes.ui.main.MainScreen
import by.kimentiy.notes.ui.theme.NotesTheme
import kotlinx.coroutines.GlobalScope

fun main() = application {
    val notesRepository = SqlDelightNotesRepository(
        driverFactory = SqlDelightDriverFactory(),
        scope = GlobalScope
    )

    val inboxViewModel = InboxViewModel(GlobalScope, notesRepository)
    val checklistsViewModel = ChecklistsViewModel(GlobalScope, notesRepository)
    val notesViewModel = NotesViewModel(GlobalScope, notesRepository)

    Window(onCloseRequest = ::exitApplication) {
        NotesTheme {
            val currentScreen = remember { mutableStateOf<Screens>(Screens.Main) }

            MainScreen(
                inboxViewModel = inboxViewModel,
                checklistsViewModel = checklistsViewModel,
                notesViewModel = notesViewModel,
                repository = notesRepository,
                state = currentScreen
            )

            when (val screen = currentScreen.value) {
                Screens.Main -> {
                    // do nothing
                }
                is Screens.EditNote -> {
                    val viewModel = screen.id?.let { notesViewModel.getNoteById(it) }
                        ?: NoteViewModel(
                            note = null,
                            scope = GlobalScope,
                            repository = notesRepository
                        )

                    EditNoteScreen(
                        viewModel = viewModel,
                        onBackClicked = {
                            viewModel.saveChanges()

                            currentScreen.value = Screens.Main
                        }
                    )
                }
                Screens.AddPurchase -> {
                    val text = remember { mutableStateOf("") }

                    AddPurchaseDialog(
                        text = text,
                        navigateUp = {
                            currentScreen.value = Screens.Main
                        }
                    )
                }
                Screens.Inbox -> {
                    InboxScreen(
                        viewModel = inboxViewModel,
                        navigateToTaskEditing = {
                            currentScreen.value = Screens.EditInbox(it)
                        },
                        onBackPressed = {
                            inboxViewModel.saveData()
                            currentScreen.value = Screens.Main
                        }
                    )
                }
                is Screens.EditInbox -> {
                    val taskId = screen.id
                    var taskViewModel by remember {
                        mutableStateOf<InboxTaskViewModel?>(null)
                    }
                    val handleBackPress: () -> Unit = {
                        taskViewModel?.saveData()
                        currentScreen.value = Screens.Inbox
                    }

                    var editingSubtask by remember { mutableStateOf<SubtaskViewModel?>(null) }

                    if (taskViewModel != null) {
                        EditInboxItemScreen(
                            viewModel = taskViewModel!!,
                            editSubtask = { model ->
                                editingSubtask = model
                            },
                            onBackPressed = handleBackPress
                        )
                    }

                    editingSubtask?.let { subtask ->
                        EditSubtaskDialog(
                            subtask = subtask,
                            onCancelClicked = {
                                editingSubtask = null
                            },
                            onAcceptClicked = {

                            }
                        )
                    }
                    LaunchedEffect(null) {
                        taskViewModel = inboxViewModel.getTaskById(taskId)
                    }
                }
                Screens.EditChecklist -> {

                }
            }
        }
    }
}

@Composable
fun MainScreen(
    inboxViewModel: InboxViewModel,
    checklistsViewModel: ChecklistsViewModel,
    notesViewModel: NotesViewModel,
    repository: NotesRepository,
    state: MutableState<Screens>
) {
    MainScreen(
        inboxViewModel = inboxViewModel,
        checklistsViewModel = checklistsViewModel,
        notesViewModel = notesViewModel,
        repository = repository,
        onRefreshClicked = {

        },
        onInboxClicked = {

        },
        onChecklistClicked = {

        },
        onSearchClicked = {

        },
        navigateToEditScreen = {
            state.value = Screens.EditNote(it)
        },
        navigateToAddPurchaseScreen = {
            state.value = Screens.AddPurchase
        },
        onAddInboxClicked = {
            state.value = Screens.EditInbox(id = null)
        }
    )
}
