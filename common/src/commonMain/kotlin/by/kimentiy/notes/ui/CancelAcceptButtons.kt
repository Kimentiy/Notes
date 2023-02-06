package by.kimentiy.notes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun CancelAcceptButtons(
    onAcceptClicked: () -> Unit,
    onCancelClicked: () -> Unit
) {
    Row(modifier = Modifier.height(50.dp)) {
        BoxWithIcon(
            icon = Icons.Default.Close,
            contentDescription = "Cancel",
            backgroundColor = Color.Red,
            onClick = onCancelClicked
        )
        BoxWithIcon(
            icon = Icons.Default.Check,
            contentDescription = "Accept",
            backgroundColor = Color.Green,
            onClick = onAcceptClicked
        )
    }
}

@Composable
private fun RowScope.BoxWithIcon(
    icon: ImageVector,
    contentDescription: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    Box(
        Modifier
            .weight(1f)
            .fillMaxHeight()
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp),
            tint = Color.White
        )
    }
}
