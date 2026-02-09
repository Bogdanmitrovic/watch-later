package com.bogdanmitrovic.watchlater

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class Article @OptIn(ExperimentalTime::class) constructor(
    val id: String,
    val url: String,
    val title: String,
    val isRead: Boolean = false,
    val savedAt: Long = Clock.System.now().toEpochMilliseconds()
)