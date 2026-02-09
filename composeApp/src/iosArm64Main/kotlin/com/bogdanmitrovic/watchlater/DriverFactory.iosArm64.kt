package com.bogdanmitrovic.watchlater

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.bogdanmitrovic.WatchLaterDatabase

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = WatchLaterDatabase.Schema,
            name = "watchlater.db"
        )
    }
}