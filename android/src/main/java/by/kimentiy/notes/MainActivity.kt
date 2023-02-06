package by.kimentiy.notes

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.provider.UserDictionary
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import by.kimentiy.notes.ui.*
import by.kimentiy.notes.ui.main.MainScreen
import by.kimentiy.notes.ui.theme.NotesTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as NotesApp).repository
        val viewModelFactory = NotesViewModelFactory(repository)

        setContent {
            val inboxViewModel = viewModel<InboxViewModelWrapper>(factory = viewModelFactory).model
            val checklistsViewModel =
                viewModel<ChecklistsViewModelWrapper>(factory = viewModelFactory).model
            val notesViewModel = viewModel<NotesViewModelWrapper>(factory = viewModelFactory).model

            NotesTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screens.Main.route
                ) {
                    composable(Screens.Main.route) {
                        MainScreen(
                            inboxViewModel = inboxViewModel,
                            checklistsViewModel = checklistsViewModel,
                            notesViewModel = notesViewModel,
                            repository = repository,
                            onInboxClicked = {
                                navController.navigate(Screens.Inbox.route)
                            },
                            onChecklistClicked = {
                                navController.navigate(Screens.EditChecklist.route)
                            },
                            onSearchClicked = {

                            },
                            navigateToEditScreen = {
                                navController.navigate(Screens.EditNote(it).route)
                            },
                            navigateToAddPurchaseScreen = {
                                navController.navigate(Screens.AddPurchase.route)
                            },
                            onAddInboxClicked = {
                                navController.navigate(Screens.EditInbox(null).route)
                            }
                        )
                    }
                    composable(
                        Screens.EditNote.routeTemplate,
                        arguments = createNavArgumentsWithId()
                    ) { backStackEntry ->
                        val noteId = requireNotNull(extractId(backStackEntry.arguments))
                        val viewModel = notesViewModel.getNoteById(noteId)
                        val handleBackPress: () -> Unit = {
                            viewModel.saveChanges()
                            navController.navigateUp()
                        }

                        BackPressHandler(handleBackPress)
                        EditNoteScreen(
                            viewModel = viewModel,
                            onBackClicked = handleBackPress
                        )
                    }
                    composable(Screens.Inbox.route) {
                        val handleBackPress: () -> Unit = {
                            inboxViewModel.saveData()
                            navController.navigateUp()
                        }

                        InboxScreen(
                            viewModel = inboxViewModel,
                            navigateToTaskEditing = {
                                navController.navigate(Screens.EditInbox(it).route)
                            },
                            onBackPressed = handleBackPress
                        )
                        BackPressHandler(handleBackPress)
                    }
                    composable(
                        Screens.EditInbox.routeTemplate,
                        arguments = createNavArgumentsWithId()
                    ) { backStackEntry ->
                        val taskId = extractId(backStackEntry.arguments)
                        var taskViewModel by remember {
                            mutableStateOf<InboxTaskViewModel?>(null)
                        }
                        val handleBackPress: () -> Unit = {
                            taskViewModel?.saveData()
                            navController.navigateUp()
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
                            BackPressHandler(handleBackPress)
                        }
                        editingSubtask?.let {
                            EditSubtaskDialog(
                                subtask = it,
                                onAcceptClick = {
                                    taskViewModel?.onApplySubtaskClicked(it)
                                    editingSubtask = null
                                },
                                onDismiss = {
                                    it.reset()
                                    editingSubtask = null
                                }
                            )
                        }
                        LaunchedEffect(null) {
                            taskViewModel = inboxViewModel.getTaskById(taskId)
                        }
                    }
                    composable(Screens.EditChecklist.route) {
                        var viewModel by remember {
                            mutableStateOf<ChecklistViewModel?>(null)
                        }
                        val handleBackPressed: () -> Unit = {
                            viewModel?.saveChanges()
                            navController.navigateUp()
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
                            BackPressHandler(handleBackPressed)
                        }

                        LaunchedEffect(null) {
                            viewModel = checklistsViewModel.getBuylist()
                        }
                    }
                    dialog(Screens.AddPurchase.route) {
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
                                    navController.navigateUp()
                                },
                                onAcceptClicked = {
                                    viewModel.value?.addItem(model.title.value)
                                    navController.navigateUp()
                                }
                            )
                        }
                        LaunchedEffect(null) {
                            viewModel.value = checklistsViewModel.getBuylist()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotesTheme {
        // TODO
    }
}
