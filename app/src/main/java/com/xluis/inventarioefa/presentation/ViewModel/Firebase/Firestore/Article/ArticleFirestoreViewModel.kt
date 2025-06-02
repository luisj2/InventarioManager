package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.UiEvent
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Article.ArticleFirestoreRespository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ArticleFirestoreViewModel(private val repository: ArticleFirestoreRespository) : ViewModel() {

    private val _insertArticleStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val insertArticleStatus: StateFlow<SuspendResult<Boolean>> = _insertArticleStatus

    private val _articleById = MutableStateFlow<SuspendResult<Article?>>(SuspendResult.Idle)
    val articleById: StateFlow<SuspendResult<Article?>> = _articleById

    private val _allArticlesList = MutableStateFlow<SuspendResult<List<Article>>>(SuspendResult.Loading)
    val allArticlesList: StateFlow<SuspendResult<List<Article>>> = _allArticlesList

    private val _addArticleReturnStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val addArticleReturnStatus: StateFlow<SuspendResult<Boolean>> = _addArticleReturnStatus

    private val _addArticleTakedStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val addArticleTakedStatus: StateFlow<SuspendResult<Boolean>> = _addArticleTakedStatus

    private val _substractArticleCountStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val substractArticleCountStatus: StateFlow<SuspendResult<Boolean>> = _substractArticleCountStatus

    private val _removeArticleTakedStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val removeArticleTakedStatus: StateFlow<SuspendResult<Boolean>> = _removeArticleTakedStatus

    private val _addArticleCountStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val addArticleCountStatus: StateFlow<SuspendResult<Boolean>> = _addArticleCountStatus

    private val _allArticleTakedList = MutableStateFlow<SuspendResult<List<ArticleTaked>>>(SuspendResult.Loading)
    val allArticleTakedList: StateFlow<SuspendResult<List<ArticleTaked>>> = _allArticleTakedList

    private val _allArticleReturnList = MutableStateFlow<SuspendResult<List<ArticleReturn>>>(SuspendResult.Loading)
    val allArticleReturnList: StateFlow<SuspendResult<List<ArticleReturn>>> = _allArticleReturnList

    private val _allArticleMovementList = MutableStateFlow<SuspendResult<List<ArticleMovement>>>(SuspendResult.Loading)
    val allArticleMovementList: StateFlow<SuspendResult<List<ArticleMovement>>> = _allArticleMovementList

    private val _removeArticleStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val removeArticleStatus: StateFlow<SuspendResult<Boolean>> = _removeArticleStatus

    private val _updateArticleStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val updateArticleStatus: StateFlow<SuspendResult<Boolean>> = _updateArticleStatus


    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    fun insertArticleInFirestore(article: Article) {
        viewModelScope.launch {
            _insertArticleStatus.value = SuspendResult.Loading
            _insertArticleStatus.value = repository.insertArticle(article)
        }
    }

    fun getArticleById(articleId: String) {
        viewModelScope.launch {
            _articleById.value = SuspendResult.Loading
            _articleById.value = repository.getArticleById(articleId)
        }
    }

    fun getAllArticles() {
        viewModelScope.launch {
            _allArticlesList.value = SuspendResult.Loading
            _allArticlesList.value = repository.getAllArticles()
        }
    }

    fun addArticleReturn(articleId: String, articleReturn: ArticleReturn) {
        viewModelScope.launch {
            _addArticleReturnStatus.value = SuspendResult.Loading
            _addArticleReturnStatus.value = repository.addArticleReturn(articleId, articleReturn)
        }
    }

    fun addArticleTaked(articleId: String, articleTaked: ArticleTaked) {
        viewModelScope.launch {
            _addArticleTakedStatus.value = SuspendResult.Loading
            _addArticleTakedStatus.value = repository.addArticleTaked(articleId, articleTaked)
        }
    }

    fun substractArticleCountById(articleId: String, substractCount: Int) {
        viewModelScope.launch {
            _substractArticleCountStatus.value = SuspendResult.Loading
            _substractArticleCountStatus.value = repository.substractArticleCount(articleId, substractCount)
        }
    }

    fun addArticleCountById(articleId: String, addCount: Int) {
        viewModelScope.launch {
            _addArticleCountStatus.value = SuspendResult.Loading
            _addArticleCountStatus.value = repository.addArticleCount(articleId, addCount)
        }
    }

    fun getAllArticleTakedList(articleId: String) {
        viewModelScope.launch {
            _allArticleTakedList.value = SuspendResult.Loading
            _allArticleTakedList.value = repository.getAllArticleTaked(articleId)
        }
    }

    fun getAllArticleReturnList(articleId: String) {
        viewModelScope.launch {
            _allArticleReturnList.value = SuspendResult.Loading
            _allArticleReturnList.value = repository.getAllArticleReturn(articleId)
        }
    }

    fun getAllArticleMovementListByArticleId(articleId: String,userId : String) {
        viewModelScope.launch {
            _allArticleMovementList.value = SuspendResult.Loading
            _allArticleMovementList.value = repository.getAllArticlesMovementsByArticleId(articleId,userId)
        }
    }

    fun removeArticleById(articleId: String) {
        viewModelScope.launch {
            _removeArticleStatus.value = SuspendResult.Loading
            val result = repository.deleteArticleById(articleId)
            _removeArticleStatus.value = result
        }
    }

    fun removeArticleTakedById(articleId: String, articleTakedId: String) {
        viewModelScope.launch {
            _removeArticleTakedStatus.value = SuspendResult.Loading
            _removeArticleTakedStatus.value = repository.removeArticleTakedById(articleId, articleTakedId)
        }
    }

    fun updateArticleById(articleId: String, article: Article) {
        viewModelScope.launch {
            _updateArticleStatus.value = SuspendResult.Loading
            val result = repository.updateArticleById(articleId, article)
            _updateArticleStatus.value = result
        }
    }
}
