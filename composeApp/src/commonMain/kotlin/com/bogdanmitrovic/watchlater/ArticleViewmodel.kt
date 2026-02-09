package com.bogdanmitrovic.watchlater

import com.benasher44.uuid.UUID
import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticleViewModel(
    private val repository: ArticleRepository,
    private val scope: CoroutineScope
) {
    val articles: StateFlow<List<Article>> = repository.getArticles()
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addArticle(url: String, title: String) {
        scope.launch {
            val article = Article(
                id = uuid4().toString(),
                url = url,
                title = title
            )
            repository.addArticle(article)
        }
    }

    fun markAsRead(id: String) {
        scope.launch {
            repository.markAsRead(id)
        }
    }

    fun deleteArticle(id: String) {
        scope.launch {
            repository.deleteArticle(id)
        }
    }
}