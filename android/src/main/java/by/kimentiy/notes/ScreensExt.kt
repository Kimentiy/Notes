package by.kimentiy.notes

import android.os.Bundle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import by.kimentiy.notes.repositories.Id
import by.kimentiy.notes.ui.Screens.Companion.TASK_ID_ARG

fun createNavArgumentsWithId(): List<NamedNavArgument> {
    return listOf(navArgument(TASK_ID_ARG) {
        type = NavType.LongType
        defaultValue = -1
    })
}

fun extractId(bundle: Bundle?): Id? {
    return bundle?.getLong(TASK_ID_ARG)?.let { Id(it) }
}
