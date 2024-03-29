package by.kimentiy.notes.ui.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Note(
    title: String,
    content: String,
    isSelected: Boolean,
    isConflict: Boolean = false,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFADD8E6) else Color.White
    val (borderWidth, borderColor) = if (isSelected) {
        2.dp to Color.Blue
    } else {
        1.dp to Color.Gray
    }
    Card(
        modifier = Modifier
            .heightIn(max = 150.dp)
            .background(backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        border = BorderStroke(borderWidth, borderColor)
    ) {
        Box {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.h6
                )
                Divider()
                Text(content)
            }

            if (isConflict) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = "Conflict"
                )
            }
        }
    }
}
