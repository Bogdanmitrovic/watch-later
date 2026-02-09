package com.bogdanmitrovic.watchlater

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.bogdanmitrovic.WatchLaterDatabase

actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = WatchLaterDatabase.Schema,
            context = context,
            name = "watchlater.db"
        )
    }
}