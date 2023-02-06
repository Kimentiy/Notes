package by.kimentiy.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import by.kimentiy.notes.ui.EditSubtaskDialogContent

@Composable
fun EditSubtaskDialog(
    subtask: SubtaskViewModel,
    onCancelClicked: () -> Unit,
    onAcceptClicked: () -> Unit
) {
    val titleState = subtask.title.collectAsState()
    val descriptionState = subtask.description.collectAsState()

    Window(
        onCloseRequest = onCancelClicked,
        resizable = false,
        title = "Subtask",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(400.dp, 300.dp)
        )
    ) {
        EditSubtaskDialogContent(
            title = titleState.value,
            onTitleChange = {
                subtask.title.value = it
            },
            description = descriptionState.value,
            onDescriptionChange = {
                subtask.description.value = it
            },
            onCancelClicked = onCancelClicked,
            onAcceptClicked = onAcceptClicked
        )
    }
}
