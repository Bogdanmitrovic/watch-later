package com.bogdanmitrovic.watchlater

import app.cash.sqldelight.db.SqlDriver
import com.bogdanmitrovic.WatchLaterDatabase

expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): WatchLaterDatabase {
    val driver = driverFactory.createDriver()
    val database = WatchLaterDatabase(driver)
    return database
}