package by.kimentiy.notes.ui

import by.kimentiy.notes.Id

sealed class Screens {

    abstract val route: String

    object Main : Screens() {
        override val route: String = "main"
    }

    object Inbox : Screens() {
        override val route: String = "inbox"
    }

    class EditNote(val id: Id?) : Screens() {
        override val route: String
            get() {
                return "$ROOT?$TASK_ID_ARG=${id?.id ?: -1}"
            }

        companion object {
            private const val ROOT = "edit_note"

            const val routeTemplate = "$ROOT?$TASK_ID_ARG={$TASK_ID_ARG}"
        }
    }

    class EditInbox(val id: Id?) : Screens() {

        override val route: String
            get() {
                return "$ROOT?$TASK_ID_ARG=${id?.id ?: -1}"
            }

        companion object {
            private const val ROOT = "edit_inbox"

            const val routeTemplate = "$ROOT?$TASK_ID_ARG={$TASK_ID_ARG}"
        }
    }

    object EditChecklist : Screens() {
        override val route: String = "edit_checklist"
    }

    object AddPurchase : Screens() {

        override val route: String = "add_purchase"
    }

    companion object {
        const val TASK_ID_ARG = "id"
    }
}
