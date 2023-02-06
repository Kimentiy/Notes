package by.kimentiy.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import by.kimentiy.notes.models.ChecklistViewModel

@Composable
fun EditChecklistScreen(
    checklistViewModel: ChecklistViewModel,
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
                CheckboxWithEditableTitle(
                    title = item.title.collectAsState().value,
                    onTitleChanged = {
                        item.setTitle(it)
                    },
                    isChecked = item.isChecked.collectAsState().value,
                    onCheckedChange = {
                        item.setIsChecked(it)
                    },
                    onDeleteClicked = {
                        checklistViewModel.removeItem(item)
                    }
                )
            }
            item {
                AddNewChecklistItem(
                    onClick = {
                        checklistViewModel.addItem("")
                    }
                )
            }
        }
    }
}

@Composable
private fun AddNewChecklistItem(
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Text(
            "Add new item"
        )
    }
}
