import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import by.kimentiy.notes.AddPurchaseDialog
import by.kimentiy.notes.EditSubtaskDialog
import by.kimentiy.notes.SqlDelightDriverFactory
import by.kimentiy.notes.SqlDelightNotesRepository
import by.kimentiy.notes.models.*
import by.kimentiy.notes.repositories.ChecklistItem
import by.kimentiy.notes.repositories.NotesRepository
import by.kimentiy.notes.ui.*
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
                screenState = currentScreen
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
                    val viewModel = remember {
                        mutableStateOf<ChecklistViewModel?>(null)
                    }
                    if (viewModel.value != null) {
                        val model = ChecklistItemViewModel(
                            item = ChecklistItem(
                                title = "",
                                isChecked = false
                            )
                        )
                        AddPurchaseDialog(
                            model = model,
                            onDismiss = {
                                currentScreen.value = Screens.Main
                            },
                            onAcceptClicked = {
                                viewModel.value?.addItem(model.title.value)
                                viewModel.value?.saveChanges()

                                currentScreen.value = Screens.Main
                            }
                        )
                    }
                    LaunchedEffect(null) {
                        viewModel.value = checklistsViewModel.getBuylist()
                    }
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
                    var viewModel by remember {
                        mutableStateOf<ChecklistViewModel?>(null)
                    }
                    val handleBackPressed: () -> Unit = {
                        viewModel?.saveChanges()
                        currentScreen.value = Screens.Main
                    }
                    if (viewModel != null) {
                        EditChecklistScreen(
                            checklistViewModel = viewModel!!,
                            addPurchaseDialog = { model, closeDialog ->
                                AddPurchaseDialog(
                                    model = model,
                                    onDismiss = {
                                        model.resetTitle()
                                        closeDialog()
                                    },
                                    onAcceptClicked = {
                                        closeDialog()
                                    }
                                )
                            },
                            onBackPressed = handleBackPressed
                        )
                    }

                    LaunchedEffect(null) {
                        viewModel = checklistsViewModel.getBuylist()
                    }
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
    screenState: MutableState<Screens>
) {
    MainScreen(
        inboxViewModel = inboxViewModel,
        checklistsViewModel = checklistsViewModel,
        notesViewModel = notesViewModel,
        repository = repository,
        onRefreshClicked = {

        },
        onAddInboxClicked = {
            screenState.value = Screens.EditInbox(id = null)
        },
        onInboxClicked = {
            screenState.value = Screens.Inbox
        },
        navigateToAddPurchaseScreen = {
            screenState.value = Screens.AddPurchase
        },
        onChecklistClicked = {
            screenState.value = Screens.EditChecklist
        },
        onSearchClicked = {

        },
        navigateToEditScreen = {
            screenState.value = Screens.EditNote(it)
        },
    )
}
