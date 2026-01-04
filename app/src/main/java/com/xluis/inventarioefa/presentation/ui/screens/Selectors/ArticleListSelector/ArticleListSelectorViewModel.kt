package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneNameById
import com.xluis.inventarioefa._domain.UseCases.Room.Article.CreateArticleCase
import com.xluis.inventarioefa._domain.UseCases.Room.Article.GetAllArticles
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZoneNameById
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.Enums.SortType
import com.xluis.inventarioefa._domain.util.articleToArticleMovement
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Datastore.UserDataStore
import com.xluis.inventarioefa.data.Mapper.Article.toEntity
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.utils.YOUR_MOVEMENT_ROOM
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ArticleListSelectorViewModel(
    private val getRoomArticles: GetAllArticles,
    private val getFirestoreZoneNameById: GetFirestoreZoneNameById,
    private val getRoomZoneNameById: GetRoomZoneNameById,
    private val createArticle: CreateArticleCase
) : ViewModel() {

    private val _uiState = mutableStateOf(ArticleListSelectorUiState())
    val uiState: State<ArticleListSelectorUiState> = _uiState


    private val _uiEffect = Channel<ArticleListSelectorUiEffect> {}
    val uiEffect = _uiEffect.receiveAsFlow()
    private fun updateState(update: ArticleListSelectorUiState.() -> ArticleListSelectorUiState) {
        _uiState.value = _uiState.value.update()
    }

    init {
        viewModelScope.launch {
            UserDataStore.getUserUid().collect { uid ->
                updateState { copy(userId = uid) }
            }
        }
    }


    fun onEvent(event: ArticleListSelectorUiEvent) {
        when (event) {
            ArticleListSelectorUiEvent.GetAllArticles -> {
                getAllArticles()
            }
            ArticleListSelectorUiEvent.NavigateBack -> navigateBack()
            is ArticleListSelectorUiEvent.DeselectArticleByIdList -> deselectArticle(event.articleId)
            is ArticleListSelectorUiEvent.SelectArticleList -> selectArticle(
                event.article,
                event.count
            )

            is ArticleListSelectorUiEvent.SaveSelectedArticles -> callArticleSaved()
            is ArticleListSelectorUiEvent.ShowToast -> showToast(event.message)
            is ArticleListSelectorUiEvent.ToggleCreateArticleDialog -> updateState {
                copy(
                    createArticleDialogState = event.state
                )
            }

            is ArticleListSelectorUiEvent.AddArticle -> {
                addArticleInDatabase(event.article)
            }
            is ArticleListSelectorUiEvent.InitValues -> updateState { copy(storageType = StorageType.fromName(event.storageType),zoneId = event.zoneId) }
            is ArticleListSelectorUiEvent.OnCategoryChanged -> {
                updateState { copy(selectedCategory = event.category) }
                applyFilters()
            }
            is ArticleListSelectorUiEvent.OnSearchQueryChanged -> {
                updateState { copy(searchQuery = event.query) }
                applyFilters()
            }
            is ArticleListSelectorUiEvent.OnAddSortType -> {
                val sortList = _uiState.value.sortList
                val newList = sortList.filter { it.sortOptionType != event.sortSelected.sortOptionType } + event.sortSelected

                updateState { copy(sortList = newList  ) }
                applyFilters()
            }
            is ArticleListSelectorUiEvent.OnRemoveSortType -> {
                updateState { copy(sortList = sortList - event.sortType) }
                applyFilters()
            }
        }
    }

    private fun addArticleInDatabase(article: Article) {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            createArticle(article.toEntity())
                .onSuccess {
                    showToast("Articulo añadido correctamente")
                    getAllArticles()
                }
                .onError { error ->
                    showToast(error.message)
                }
            updateState { copy(isLoading = false) }

        }
    }

    private fun deselectArticle(articleId: String) {
        val state = _uiState.value

        val newList = state.selectedArticleList.filter { it.id != articleId }

        updateState {
            copy(selectedArticleList = newList)
        }
    }


    private fun selectArticle(article: Article, count: Int) {
        val state = _uiState.value

        val newList = state.selectedArticleList.toMutableList()

        val index = newList.indexOfFirst { it.id == article.id }

        if (index != -1) {
            newList[index] = newList[index].copy(count = count)
        } else {
            newList.add(article.copy(count = count))
        }
        updateState {
            copy(selectedArticleList = newList)
        }
    }

    private fun getAllArticles() {
        viewModelScope.launch {
            updateState { copy(isLoading = true) }
            getRoomArticles()
                .onSuccess { articleList ->
                    updateState { copy(articleList = articleList) }
                    applyFilters()
                }
                .onError { error ->
                    showToast(error.message)
                }
            updateState { copy(isLoading = false) }
        }
    }


    fun showToast(message: String) {
        viewModelScope.launch {
            _uiEffect.send(ArticleListSelectorUiEffect.ShowToast(message))
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _uiEffect.send(ArticleListSelectorUiEffect.NavigateBack)
        }
    }

    private fun callArticleSaved() {
        viewModelScope.launch {
            val state = _uiState.value

            val userId : String = when (_uiState.value.storageType){
                StorageType.LOCAL -> YOUR_MOVEMENT_ROOM
                StorageType.FIREBASE -> {
                    state.userId ?: run {
                        showToast("Usuario no identificado")
                        return@launch
                    }
                }
            }

            val movements = state.selectedArticleList.map { article ->
                articleToArticleMovement(
                    article = article,
                    userId = userId,
                    action = MovementAction.ADD,
                    zoneId = state.zoneId,
                    zoneName = getZoneNameById(state.zoneId)
                )
            }

            _uiEffect.send(
                ArticleListSelectorUiEffect.ArticlesSaved(
                    selectedArticles = state.selectedArticleList,
                    selectedMovements = movements
                )
            )
        }
    }
    private suspend fun getZoneNameById (zoneId : String) : String{
        return when(_uiState.value.storageType){
            StorageType.LOCAL -> {
                val roomId = zoneId.toLongOrNull() ?: return "???"
                getRoomZoneNameById(roomId)
            }
            StorageType.FIREBASE -> {
                getFirestoreZoneNameById(zoneId)
            }
        }.getOrNull() ?: "???"
    }
    private fun applyFilters() {
        val state = _uiState.value

        var filtered = state.articleList

        // Filtrar por búsqueda
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.name.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Filtrar por categoría
        state.selectedCategory?.let { category ->
            filtered = filtered.filter { it.category == category }
        }

        // Aplicar ordenamientos según SortType
        state.sortList.forEach { sort ->
            filtered = when (sort) {
                SortType.BY_NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
                SortType.BY_NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                SortType.BY_COUNT_ASC -> filtered.sortedBy { it.count }
                SortType.BY_COUNT_DESC -> filtered.sortedByDescending { it.count }
            }
        }

        updateState { copy(filteredArticleList = filtered) }
    }

}



