package by.kimentiy.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import by.kimentiy.notes.ui.theme.NotesTheme
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = (application as NotesApp).repository
        val syncRepository = (application as NotesApp).syncRepository
        val viewModelFactory = NotesViewModelFactory(repository)

        setContent {
            val inboxViewModel = viewModel<InboxViewModelWrapper>(factory = viewModelFactory).model
            val checklistsViewModel =
                viewModel<ChecklistsViewModelWrapper>(factory = viewModelFactory).model
            val notesViewModel = viewModel<NotesViewModelWrapper>(factory = viewModelFactory).model

            val configuration = OdysseyConfiguration(canvas = this)

            setNavigationContent(configuration) {
                navigationGraph(
                    inboxViewModel = inboxViewModel,
                    checklistsViewModel = checklistsViewModel,
                    notesViewModel = notesViewModel,
                    repository = repository,
                    syncRepository = syncRepository
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NotesTheme {
        // TODO
    }
}
