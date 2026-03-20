package com.xluis.inventarioefa.presentation.ViewModel

import androidx.lifecycle.ViewModel
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ArticleSelectionSharedViewModel : ViewModel() {
    private val _selectedArticlesByScreen = MutableStateFlow<Map<String, List<Article>>>(emptyMap())
    val selectedArticlesByScreen: StateFlow<Map<String, List<Article>>> = _selectedArticlesByScreen

    fun addSelectedArticles(screenId: String, newArticles: List<Article>) {
        _selectedArticlesByScreen.value =
            _selectedArticlesByScreen.value.toMutableMap().apply {

                val currentArticles = get(screenId).orEmpty()

                val updatedList = currentArticles.toMutableList()

                newArticles.forEach { newArticle ->
                    val index = updatedList.indexOfFirst { it.id == newArticle.id }

                    if (index != -1) {
                        // Ya existe → sumar cantidad
                        val existing = updatedList[index]
                        updatedList[index] =
                            existing.copy(count = existing.count + newArticle.count)
                    } else {
                        // No existe → añadir
                        updatedList.add(newArticle)
                    }
                }

                put(screenId, updatedList)
            }
    }



    fun getSelectedArticles(screenId: String): List<Article> =
        _selectedArticlesByScreen.value[screenId] ?: emptyList()

    fun clearArticleListByScreenId(screenId: String) {
        _selectedArticlesByScreen.value = _selectedArticlesByScreen.value.toMutableMap().apply {
            remove(screenId)
        }
    }

    fun removeArticleByScreenId(screenId: String, articleId: String) {
        _selectedArticlesByScreen.value = _selectedArticlesByScreen.value.toMutableMap().apply {
            val currentArticles = get(screenId) ?: emptyList()
            // Filtramos la lista quitando el artículo con el id dado
            put(screenId, currentArticles.filter { it.id != articleId })
        }
    }
    fun removeArticlesByIds(screenId: String, articleIds: List<String>) {
        _selectedArticlesByScreen.value = _selectedArticlesByScreen.value.toMutableMap().apply {
            val currentArticles = get(screenId) ?: emptyList()
            // Filtramos quitando todos los artículos que estén en articleIds
            put(screenId, currentArticles.filter { it.id !in articleIds })
        }
    }



}