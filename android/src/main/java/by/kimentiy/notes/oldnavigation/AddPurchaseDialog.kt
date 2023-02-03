package by.kimentiy.notes.oldnavigation

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import by.kimentiy.notes.models.ChecklistItemViewModel
import by.kimentiy.notes.ui.AddPurchaseDialogContent

@Composable
fun AddPurchaseDialog(
    model: ChecklistItemViewModel,
    onDismiss: () -> Unit,
    onAcceptClicked: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismiss()
        },
        title = {
            Text("Purchase")
        },
        text = {
            AddPurchaseDialogContent(
                text = model.title.collectAsState().value,
                onTextChanged = {
                    model.setTitle(it)
                },
                onCancelClicked = {
                    model.resetTitle()
                    onDismiss()
                },
                onAcceptClicked = {
                    onAcceptClicked()
                }
            )
        },
        buttons = {}
    )
}
