package by.kimentiy.notes.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NotesBottomBar(
    onSearchClicked: () -> Unit,
    onAddNoteClicked: () -> Unit,
    onAddPurchaseClicked: () -> Unit,
    onAddInboxClicked: () -> Unit
) {
    BottomAppBar() {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onSearchClicked) {
            Icon(Icons.Default.Search, contentDescription = "Search")
        }
        IconButton(onClick = onAddNoteClicked) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }
        IconButton(onClick = onAddPurchaseClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, contentDescription = "Add buy item")
                Text(
                    "Buy",
                    style = MaterialTheme.typography.overline
                )
            }
        }
        IconButton(onClick = onAddInboxClicked) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Add, contentDescription = "Add inbox")
                Text(
                    "Inbox",
                    style = MaterialTheme.typography.overline
                )
            }
        }
    }
}

@Composable
fun SelectionBottomBar(
    onDeleteClicked: () -> Unit
) {
    BottomAppBar {
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onDeleteClicked) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}
