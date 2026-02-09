package com.bogdanmitrovic.watchlater

import kotlin.time.Clock
import kotlinx.coroutines.delay
import kotlin.time.ExperimentalTime


interface ArticleApi {
    suspend fun fetchArticles(): List<Article>
}
class FakeArticleApi : ArticleApi {

    private var counter = 2

    @OptIn(ExperimentalTime::class)
    private var articles = mutableListOf(
        Article(
            id = "remote-1",
            url = "https://example.com/article1",
            title = "Remote Article 1",
            isRead = false,
            savedAt = Clock.System.now().toEpochMilliseconds()
        ),
        Article(
            id = "remote-2",
            url = "https://example.com/article2",
            title = "Remote Article 2",
            isRead = false,
            savedAt = Clock.System.now().toEpochMilliseconds() - 1000
        )
    )

    @OptIn(ExperimentalTime::class)
    override suspend fun fetchArticles(): List<Article> {
        delay(1000)

        articles = articles.map {
            it.copy(title = it.title + " (updated)")
        }.toMutableList()

        if (counter < 4) {
            counter++
            articles.add(
                Article(
                    id = "remote-$counter",
                    url = "https://example.com/article$counter",
                    title = "Remote Article $counter",
                    isRead = false,
                    savedAt = Clock.System.now().toEpochMilliseconds()
                )
            )
        }

        println("🌐 API: returning ${articles.size} articles")
        return articles.toList()
    }

}
