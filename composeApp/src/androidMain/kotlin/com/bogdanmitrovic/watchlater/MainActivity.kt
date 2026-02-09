package com.bogdanmitrovic.watchlater

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = createDatabase(DriverFactory(applicationContext))
        val repository = ArticleRepository(database)
        val viewModel = ArticleViewModel(repository, lifecycleScope)

        setContent {
            MaterialTheme {
                ArticleScreen(viewModel)
            }
        }
    }
}