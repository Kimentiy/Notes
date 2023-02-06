package by.kimentiy.notes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BackButton(
    onClicked: () -> Unit
) {
    IconButton(
        onClick = {
            onClicked()
        }) {
        Icon(Icons.Default.ArrowBack, "Navigate back")
    }
}

@Composable
fun CheckboxWithTitle(
    title: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onItemClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clickable(onClick = onItemClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        Text(
            title
        )
    }
}

@Composable
fun CheckboxWithEditableTitle(
    title: String,
    onTitleChanged: (String) -> Unit,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onDeleteClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
        TextField(
            value = title,
            onValueChange = onTitleChanged,
            modifier = Modifier.weight(1f),
        )
        IconButton(
            onClick = {
                onDeleteClicked()
            }
        ) {
            Icon(Icons.Default.Delete, null)
        }
    }
}
