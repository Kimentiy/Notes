package by.kimentiy.notes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@Composable
fun EditSubtaskDialogContent(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onCancelClicked: () -> Unit,
    onAcceptClicked: () -> Unit
) {
    val focusRequester = FocusRequester()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("Title") },
            modifier = Modifier.padding(16.dp).focusRequester(focusRequester)
        )
        TextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("Description") }
        )
        CancelAcceptButtons(
            onCancelClicked = onCancelClicked,
            onAcceptClicked = onAcceptClicked
        )
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
