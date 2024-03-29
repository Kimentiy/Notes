import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import by.kimentiy.notes.SqlDelightDriverFactory
import by.kimentiy.notes.SqlDelightNotesRepository
import by.kimentiy.notes.models.ChecklistsViewModel
import by.kimentiy.notes.models.InboxViewModel
import by.kimentiy.notes.models.NotesViewModel
import by.kimentiy.notes.navigationGraph
import by.kimentiy.notes.michsync.SyncMichRepository
import by.kimentiy.notes.multiplatformsettings.MultiplatformSettingsRepository
import kotlinx.coroutines.GlobalScope
import ru.alexgladkov.odyssey.compose.setup.OdysseyConfiguration
import ru.alexgladkov.odyssey.compose.setup.setNavigationContent

fun main() = application {
    val notesRepository = SqlDelightNotesRepository(
        driverFactory = SqlDelightDriverFactory(),
        scope = GlobalScope
    )
    val settingsRepository = MultiplatformSettingsRepository()

    val inboxViewModel = InboxViewModel(GlobalScope, notesRepository)
    val checklistsViewModel = ChecklistsViewModel(GlobalScope, notesRepository)
    val notesViewModel = NotesViewModel(GlobalScope, notesRepository)
    val syncRepository = SyncMichRepository(
        settingsRepository = settingsRepository,
        notesRepository = notesRepository
    )

    Window(onCloseRequest = ::exitApplication) {
        setNavigationContent(OdysseyConfiguration(), onApplicationFinish = {
            exitApplication()
        }) {
            navigationGraph(
                inboxViewModel = inboxViewModel,
                checklistsViewModel = checklistsViewModel,
                notesViewModel = notesViewModel,
                repository = notesRepository,
                settingsRepository = settingsRepository,
                syncRepository = syncRepository
            )
        }
    }
}
