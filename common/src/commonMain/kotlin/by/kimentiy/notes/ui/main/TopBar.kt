package by.kimentiy.notes.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotesTopBar(
    onRefreshClicked: () -> Unit,
    onSettingsClicked: () -> Unit
) {
    TopAppBar(
        title = { Text("Notes") },
        actions = {
            val iconPadding = Modifier.padding(8.dp)
            IconButton(onRefreshClicked) {
                Icon(
                    Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = iconPadding
                )
            }
            IconButton(onSettingsClicked) {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Settings",
                    modifier = iconPadding
                )
            }
        }
    )
}

@Composable
fun SelectionTopBar(
    selectedCount: Int,
    onCancelClicked: () -> Unit
) {
    TopAppBar(
        title = { Text("$selectedCount Selected") },
        actions = {
            val iconPadding = Modifier.padding(8.dp)
            IconButton(onClick = onCancelClicked) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Cancel",
                    modifier = iconPadding
                )
            }
        }
    )
}
