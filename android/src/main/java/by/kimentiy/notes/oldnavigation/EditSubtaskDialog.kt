package by.kimentiy.notes.oldnavigation

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import by.kimentiy.notes.models.SubtaskViewModel
import by.kimentiy.notes.ui.EditSubtaskDialogContent

@Composable
fun EditSubtaskDialog(
    subtask: SubtaskViewModel,
    onAcceptClick: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Subtask")
        },
        text = {
            EditSubtaskDialogContent(
                title = subtask.title.collectAsState().value,
                onTitleChange = {
                    subtask.title.value = it
                },
                description = subtask.description.collectAsState().value,
                onDescriptionChange = {
                    subtask.description.value = it
                },
                onCancelClicked = onDismiss,
                onAcceptClicked = onAcceptClick
            )
        },
        buttons = {}
    )
}
