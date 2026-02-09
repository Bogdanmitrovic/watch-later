package com.bogdanmitrovic.watchlater

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun main() = application {
    val database = createDatabase(DriverFactory())
    val api = FakeArticleApi()  // Create the fake API
    val repository = ArticleRepository(database, api)  // Pass it here
    val viewModel = ArticleViewModel(repository, CoroutineScope(Dispatchers.Main))

    Window(
        onCloseRequest = ::exitApplication,
        title = "Watch Later"
    ) {
        ArticleScreen(viewModel)
    }
}