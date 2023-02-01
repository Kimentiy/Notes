package by.kimentiy.notes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import by.kimentiy.notes.models.ChecklistItemViewModel
import by.kimentiy.notes.models.ChecklistViewModel

@Composable
fun EditChecklistScreen(
    checklistViewModel: ChecklistViewModel,
    addPurchaseDialog: @Composable (ChecklistItemViewModel, closeDialog: () -> Unit) -> Unit,
    onBackPressed: () -> Unit,
) {
    val items = checklistViewModel.items.collectAsState()
    var editingItem by remember { mutableStateOf<ChecklistItemViewModel?>(null) }
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onClicked = onBackPressed)
            Text(checklistViewModel.name.collectAsState().value)
        }
        LazyColumn {
            items(items = items.value) { item ->
                CheckboxWithTitle(
                    title = item.title.collectAsState().value,
                    onCheckedChange = {
                        item.setIsChecked(it)
                    },
                    isChecked = item.isChecked.collectAsState().value,
                    onItemClicked = {
                        editingItem = item
                    }
                )
            }
        }
    }
    editingItem?.let { model ->
        addPurchaseDialog(model) { editingItem = null }
    }
}
