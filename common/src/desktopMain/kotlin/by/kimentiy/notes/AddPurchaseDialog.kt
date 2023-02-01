package by.kimentiy.notes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import by.kimentiy.notes.ui.AddPurchaseDialogContent

@Composable
fun AddPurchaseDialog(
    text: MutableState<String>,
    navigateUp: () -> Unit
) {
    Window(
        onCloseRequest = { navigateUp() },
        resizable = false,
        title = "Purchase",
        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = DpSize(400.dp, 200.dp)
        )
    ) {
        AddPurchaseDialogContent(
            text = text.value,
            onTextChanged = {
                text.value = it
            },
            onAcceptClicked = {},
            onCancelClicked = {}
        )
    }
}
