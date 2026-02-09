package com.bogdanmitrovic.watchlater

import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mobilenativefoundation.store.store5.StoreReadResponse
import kotlin.collections.emptyList

class ArticleViewModel(
    private val repository: ArticleRepository,
    private val scope: CoroutineScope
) {
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _refresh = MutableStateFlow(false)

    val articles: StateFlow<List<Article>> = _refresh
        .flatMapLatest { refresh ->
            repository.getArticles(refresh)
        }
        .mapLatest { response ->
            when (response) {
                is StoreReadResponse.Loading -> {
                    _isLoading.value = true
                    emptyList()
                }
                is StoreReadResponse.Data -> {
                    _isLoading.value = false
                    response.value
                }
                else -> {
                    _isLoading.value = false
                    emptyList()
                }
            }
        }
        .stateIn(
            scope = scope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun refresh() {
        _refresh.value = true
        scope.launch {
            // Reset after a delay
            kotlinx.coroutines.delay(500)
            _refresh.value = false
        }
    }

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