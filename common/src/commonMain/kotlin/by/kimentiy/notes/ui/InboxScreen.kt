package by.kimentiy.notes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import by.kimentiy.notes.Id
import by.kimentiy.notes.InboxViewModel

@Composable
fun InboxScreen(
    viewModel: InboxViewModel,
    navigateToTaskEditing: (Id?) -> Unit,
    onBackPressed: () -> Unit
) {
    val tasksViewModels = viewModel.tasks.collectAsState()
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onClicked = onBackPressed)
            Text("Inbox")
        }
        LazyColumn {
            items(items = tasksViewModels.value) { taskViewModel ->
                CheckboxWithTitle(
                    title = taskViewModel.title.collectAsState().value,
                    onCheckedChange = {
                        taskViewModel.setIsCompleted(it)
                    },
                    isChecked = taskViewModel.isCompleted.collectAsState().value,
                    onItemClicked = {
                        navigateToTaskEditing(taskViewModel.id)
                    }
                )
            }
        }
    }
}