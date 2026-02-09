package com.bogdanmitrovic.watchlater

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.bogdanmitrovic.WatchLaterDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.impl.extensions.fresh
import com.bogdanmitrovic.Article as DbArticle

private fun DbArticle.toDomain() = Article(
    id = id,
    url = url,
    title = title,
    isRead = isRead != 0.toLong(),
    savedAt = savedAt
)

class ArticleRepository(
    private val database: WatchLaterDatabase,
    private val api: ArticleApi
) {

    // Store configuration
    private val store = StoreBuilder
        .from(
            fetcher = Fetcher.of { _: String ->
                // Fetch from API
                api.fetchArticles()
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = { _: String ->
                    // Read from database
                    database.articleQueries
                        .getAll()
                        .asFlow()
                        .mapToList(Dispatchers.Default)
                        .map { dbArticles ->
                            dbArticles.map { it.toDomain() }
                        }
                },
                writer = { _: String, articles: List<Article> ->
                    database.articleQueries.transaction {
                        articles.forEach { remote ->
                            val local = database.articleQueries
                                .getById(remote.id)
                                .executeAsOneOrNull()

                            database.articleQueries.insert(
                                id = remote.id,
                                url = remote.url,
                                title = remote.title,
                                isRead = local?.isRead ?: if (remote.isRead) 1 else 0,
                                savedAt = local?.savedAt ?: remote.savedAt
                            )
                        }
                    }
                }
            )
        )
        .build()

    // Get articles - returns cached data immediately, then fetches from API
    fun getArticles(refresh: Boolean = false): Flow<StoreReadResponse<List<Article>>> {
        return if (refresh) {
            store.stream(StoreReadRequest.fresh(key = "all"))
        } else {
            store.stream(StoreReadRequest.cached(key = "all", refresh = true))
        }
    }

    // Add new article locally
    suspend fun addArticle(article: Article) {
        database.articleQueries.insert(
            id = article.id,
            url = article.url,
            title = article.title,
            isRead = if (article.isRead) 1 else 0,
            savedAt = article.savedAt
        )
    }

    suspend fun markAsRead(id: String) {
        database.articleQueries.updateReadStatus(
            isRead = 1,
            id = id
        )
    }

    suspend fun deleteArticle(id: String) {
        database.articleQueries.deleteById(id)
    }
}

