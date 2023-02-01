
package by.kimentiy.notes

import app.cash.sqldelight.db.SqlDriver

expect class SqlDelightDriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: SqlDelightDriverFactory): Database {
    val driver = driverFactory.createDriver()

    return Database(driver)
}
