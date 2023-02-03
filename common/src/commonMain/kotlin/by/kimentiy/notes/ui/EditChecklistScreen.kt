package by.kimentiy.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import by.kimentiy.notes.models.ChecklistItemViewModel
import by.kimentiy.notes.models.ChecklistViewModel

@Composable
fun EditChecklistScreen(
    checklistViewModel: ChecklistViewModel,
    showAddPurchaseDialog: (ChecklistItemViewModel) -> Unit,
    onBackPressed: () -> Unit,
) {
    val items = checklistViewModel.items.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
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
                        showAddPurchaseDialog(item)
                    }
                )
            }
        }
    }
}
