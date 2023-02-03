package by.kimentiy.notes

import androidx.compose.material.Text
import androidx.compose.runtime.*
import by.kimentiy.notes.models.*
import by.kimentiy.notes.repositories.ChecklistItem
import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.repositories.NotesRepository
import by.kimentiy.notes.repositories.makeSomeRequest
import by.kimentiy.notes.ui.*
import by.kimentiy.notes.ui.main.MainScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.alexgladkov.odyssey.compose.extensions.present
import ru.alexgladkov.odyssey.compose.extensions.push
import ru.alexgladkov.odyssey.compose.extensions.screen
import ru.alexgladkov.odyssey.compose.local.LocalRootController
import ru.alexgladkov.odyssey.compose.navigation.RootComposeBuilder
import ru.alexgladkov.odyssey.compose.navigation.modal_navigation.AlertConfiguration


fun RootComposeBuilder.navigationGraph(
    inboxViewModel: InboxViewModel,
    checklistsViewModel: ChecklistsViewModel,
    notesViewModel: NotesViewModel,
    repository: NotesRepository,
) {
    screen(Screens.Main.name) {
        val rootController = LocalRootController.current
        val modalController = rootController.findModalController()

        MainScreen(
            inboxViewModel = inboxViewModel,
            checklistsViewModel = checklistsViewModel,
            notesViewModel = notesViewModel,
            repository = repository,
            onRefreshClicked = {
                GlobalScope.launch {
                    val result = makeSomeRequest()

                    val alertConfiguration =
                        AlertConfiguration(maxWidth = 0.8f, cornerRadius = 4)
                    modalController.present(alertConfiguration) { dialogKey ->

                        Text(result)
                    }
                }
            },
            onInboxClicked = {
                rootController.push(Screens.Inbox.name)
            },
            onChecklistClicked = {
                rootController.push(Screens.EditChecklist.name)
            },
            onSearchClicked = {

            },
            navigateToEditScreen = {
                rootController.push(Screens.EditNote(it).name, it)
            },
            navigateToAddPurchaseScreen = {
                val model = ChecklistItemViewModel(
                    item = ChecklistItem(
                        title = "",
                        isChecked = false
                    )
                )
                val alertConfiguration =
                    AlertConfiguration(maxWidth = 0.8f, cornerRadius = 4)
                modalController.present(alertConfiguration) { dialogKey ->
                    AddPurchaseDialogContent(
                        text = model.title.collectAsState().value,
                        onTextChanged = {
                            model.setTitle(it)
                        },
                        onCancelClicked = {
                            model.resetTitle()
                            modalController.popBackStack(dialogKey)
                        },
                        onAcceptClicked = {
                            GlobalScope.launch(Dispatchers.IO) {
                                checklistsViewModel.getBuylist().addItem(model.title.value)
                            }
                            modalController.popBackStack(dialogKey)
                        }
                    )
                }
            },
            onAddInboxClicked = {
                rootController.push(Screens.EditInbox(null).name)
            }
        )
    }
    screen(Screens.EditNote(null).name) {
        val rootController = LocalRootController.current
        val noteId = (it as Id?) ?: Id(-1)
        val viewModel = notesViewModel.getNoteById(noteId)
        val handleBackPress: () -> Unit = {
            viewModel.saveChanges()
            rootController.popBackStack()
        }

        EditNoteScreen(
            viewModel = viewModel,
            onBackClicked = handleBackPress
        )
    }
    screen(Screens.Inbox.name) {
        val rootController = LocalRootController.current
        InboxScreen(
            viewModel = inboxViewModel,
            navigateToTaskEditing = {
                rootController.push(Screens.EditInbox(null).name, it)
            },
            onBackPressed = {
                inboxViewModel.saveData()
                rootController.popBackStack()
            }
        )
    }
    screen(Screens.EditInbox(null).name) {
        val rootController = LocalRootController.current
        val modalController = rootController.findModalController()

        val taskId = it as Id?
        var taskViewModel by remember {
            mutableStateOf<InboxTaskViewModel?>(null)
        }

        if (taskViewModel != null) {
            EditInboxItemScreen(
                viewModel = taskViewModel!!,
                editSubtask = { subtask ->
                    val alertConfiguration =
                        AlertConfiguration(maxWidth = 0.8f, cornerRadius = 4)
                    modalController.present(alertConfiguration) { dialogKey ->
                        EditSubtaskDialogContent(
                            title = subtask.title.collectAsState().value,
                            onTitleChange = {
                                subtask.title.value = it
                            },
                            description = subtask.description.collectAsState().value,
                            onDescriptionChange = {
                                subtask.description.value = it
                            },
                            onAcceptClicked = {
                                taskViewModel?.onApplySubtaskClicked(subtask)
                                modalController.popBackStack(dialogKey)
                            },
                            onCancelClicked = {
                                subtask.reset()
                                modalController.popBackStack(dialogKey)
                            }
                        )
                    }
                },
                onBackPressed = {
                    taskViewModel?.saveData()
                    rootController.popBackStack()
                }
            )
        }

        LaunchedEffect(null) {
            taskViewModel = inboxViewModel.getTaskById(taskId)
        }
    }
    screen(Screens.EditChecklist.name) {
        val rootController = LocalRootController.current
        val modalController = rootController.findModalController()

        var viewModel by remember {
            mutableStateOf<ChecklistViewModel?>(null)
        }
        if (viewModel != null) {
            EditChecklistScreen(
                checklistViewModel = viewModel!!,
                showAddPurchaseDialog = { model ->
                    val alertConfiguration =
                        AlertConfiguration(maxWidth = 0.8f, cornerRadius = 4)
                    modalController.present(alertConfiguration) { dialogKey ->
                        AddPurchaseDialogContent(
                            text = model.title.collectAsState().value,
                            onTextChanged = {
                                model.setTitle(it)
                            },
                            onCancelClicked = {
                                model.resetTitle()
                                modalController.popBackStack(dialogKey)
                            },
                            onAcceptClicked = {
                                modalController.popBackStack(dialogKey)
                            }
                        )
                    }
                },
                onBackPressed = {
                    viewModel?.saveChanges()
                    rootController.popBackStack()
                }
            )
        }

        LaunchedEffect(null) {
            viewModel = checklistsViewModel.getBuylist()
        }
    }
}
