package by.kimentiy.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import by.kimentiy.notes.InboxTaskViewModel
import by.kimentiy.notes.SubtaskViewModel

@Composable
fun EditInboxItemScreen(
    viewModel: InboxTaskViewModel,
    editSubtask: (SubtaskViewModel) -> Unit,
    onBackPressed: () -> Unit
) {
    val scrollableState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            BackButton(onClicked = onBackPressed)
        }
        Column(
            modifier = Modifier
                .verticalScroll(scrollableState)
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
        ) {
            val fieldColors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent
            )
            TextField(
                value = viewModel.title.collectAsState().value,
                onValueChange = {
                    viewModel.setTitle(it)
                },
                placeholder = {
                    Text("Title")
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = fieldColors
            )
            Divider()
            TextField(
                value = viewModel.description.collectAsState().value,
                onValueChange = {
                    viewModel.setDescription(it)
                },
                placeholder = {
                    Text("Description")
                },
                modifier = Modifier
                    .fillMaxWidth(),
                colors = fieldColors
            )
            Row(
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    "Subtasks:",
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = {
                        editSubtask(
                            SubtaskViewModel(
                                subtask = null
                            )
                        )
                    }
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add subtask"
                    )
                }
            }

            viewModel.subtasks.collectAsState().value.forEach { subtaskUiModel ->
                SubtaskItem(
                    title = subtaskUiModel.title.value,
                    isCompleted = subtaskUiModel.isCompleted.collectAsState(),
                    onItemClicked = { editSubtask(subtaskUiModel) },
                    onIsCompletedChange = { subtaskUiModel.isCompleted.value = it }
                )
            }
        }
    }
}

@Composable
fun SubtaskItem(
    title: String,
    isCompleted: State<Boolean>,
    onItemClicked: () -> Unit,
    onIsCompletedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable(onClick = onItemClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isCompleted.value,
            onCheckedChange = onIsCompletedChange
        )
        Text(
            title
        )
    }
}
