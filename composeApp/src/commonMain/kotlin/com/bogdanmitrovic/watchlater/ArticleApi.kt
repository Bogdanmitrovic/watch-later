package com.bogdanmitrovic.watchlater

import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime


interface ArticleApi {
    suspend fun fetchArticles(): List<Article>
}

// Fake API that simulates network delay
class FakeArticleApi : ArticleApi {
    @OptIn(ExperimentalTime::class)
    override suspend fun fetchArticles(): List<Article> {
        delay(1000) // Simulate network delay

        // Return some fake articles
        return listOf(
            Article(
                id = "remote-1",
                url = "https://example.com/article1",
                title = "Remote Article 1 (from API)",
                isRead = false,
                savedAt = Clock.System.now().toEpochMilliseconds()
            ),
            Article(
                id = "remote-2",
                url = "https://example.com/article2",
                title = "Remote Article 2 (from API)",
                isRead = false,
                savedAt = Clock.System.now().toEpochMilliseconds() - 1000
            )
        )
    }
}