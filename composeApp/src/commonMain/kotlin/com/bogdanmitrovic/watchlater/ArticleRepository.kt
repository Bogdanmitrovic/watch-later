package com.bogdanmitrovic.watchlater

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bogdanmitrovic.WatchLaterDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.bogdanmitrovic.Article as DbArticle

private fun DbArticle.toDomain() = Article(
    id = id,
    url = url,
    title = title,
    isRead = isRead != 0.toLong(),
    savedAt = savedAt
)

class ArticleRepository(private val database: WatchLaterDatabase) {
    fun getArticles(): Flow<List<Article>> {
        return database.articleQueries
            .getAll()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { dbArticles ->
                dbArticles.map { it.toDomain() }
            }
    }

    // Add new article
    suspend fun addArticle(article: Article) {
        database.articleQueries.insert(
            id = article.id,
            url = article.url,
            title = article.title,
            isRead = if (article.isRead) 1 else 0,
            savedAt = article.savedAt
        )
    }

    // Mark as read
    suspend fun markAsRead(id: String) {
        database.articleQueries.updateReadStatus(
            isRead = 1,
            id = id
        )
    }

    // Delete article
    suspend fun deleteArticle(id: String) {
        database.articleQueries.deleteById(id)
    }
}
