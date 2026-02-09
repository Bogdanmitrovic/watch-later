package com.bogdanmitrovic.watchlater

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.bogdanmitrovic.WatchLaterDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        // Get user's home directory for database storage
        val databasePath = File(System.getProperty("user.home"), ".watchlater")
        databasePath.mkdirs()

        val databaseFile = File(databasePath, "watchlater.db")
        val databaseUrl = "jdbc:sqlite:${databaseFile.absolutePath}"

        val driver = JdbcSqliteDriver(databaseUrl)

        // Create schema if database is new
        if (!databaseFile.exists() || databaseFile.length() == 0L) {
            WatchLaterDatabase.Schema.create(driver)
        }

        return driver
    }
}