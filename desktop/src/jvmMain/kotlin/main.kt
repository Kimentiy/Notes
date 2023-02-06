import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import by.kimentiy.notes.AddPurchaseDialog
import by.kimentiy.notes.InboxTaskViewModel
import by.kimentiy.notes.EditSubtaskDialog
import by.kimentiy.notes.SubtaskViewModel
import by.kimentiy.notes.ui.EditInboxItemScreen
import by.kimentiy.notes.ui.EditNoteScreen
import by.kimentiy.notes.ui.main.MainScreen
import by.kimentiy.notes.ui.Screens
import by.kimentiy.notes.ui.theme.NotesTheme

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        NotesTheme {
            val currentScreen = remember { mutableStateOf(Screens.MAIN) }

            MainScreen(currentScreen)

            when (currentScreen.value) {
                Screens.MAIN -> {
                    // do nothing
                }
                Screens.EDIT_NOTE -> {
                    EditNoteScreen(
                        onBackClicked = {
                            currentScreen.value = Screens.MAIN
                        }
                    )
                }
                Screens.ADD_PURCHASE -> {
                    val text = remember { mutableStateOf("") }

                    AddPurchaseDialog(
                        text = text,
                        navigateUp = {
                            currentScreen.value = Screens.MAIN
                        }
                    )
                }
                Screens.EDIT_INBOX -> {
                    val viewModel = InboxTaskViewModel()
                    var editingSubtask by remember { mutableStateOf<SubtaskViewModel?>(null) }

                    EditInboxItemScreen(
                        title = viewModel.title.collectAsState(),
                        onTitleChanged = {
                            viewModel.setTitle(it)
                        },
                        description = viewModel.description.collectAsState(),
                        onDescriptionChanged = {
                            viewModel.setDescription(it)
                        },
                        subtasks = viewModel.subtasks,
                        onSubtaskClick = {
                            editingSubtask = it
                        },
                        onBackPressed = {
                            currentScreen.value = Screens.MAIN
                        }
                    )

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
                }
            }
        }
    }
}

@Composable
fun MainScreen(state: MutableState<Screens>) {
    MainScreen(
        navigateToEditScreen = {
            state.value = Screens.EDIT_NOTE
        },
        navigateToAddPurchaseScreen = {
            state.value = Screens.ADD_PURCHASE
        },
        onAddInboxClicked = {
            state.value = Screens.EDIT_INBOX
        }
    )
}
