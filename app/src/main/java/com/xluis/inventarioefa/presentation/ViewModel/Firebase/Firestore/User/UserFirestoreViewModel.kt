package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.UiEvent
import com.xluis.inventarioefa.domain.model.DataClass.User.User
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserFirestoreRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class UserFirestoreViewModel(private val repository: UserFirestoreRepository) : ViewModel() {

    private val _insertUserStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val insertUserStatus: StateFlow<SuspendResult<Boolean>> = _insertUserStatus

    private val _userByEmail = MutableStateFlow<SuspendResult<User?>>(SuspendResult.Idle)
    val userByEmail: StateFlow<SuspendResult<User?>> = _userByEmail

    private val _addArticleTakedStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val addArticleTakedStatus: StateFlow<SuspendResult<Boolean>> = _addArticleTakedStatus

    private val _addArticleReturnStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val addArticleReturnStatus: StateFlow<SuspendResult<Boolean>> = _addArticleReturnStatus

    private val _removeArticleTakedStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val removeArticleTakedStatus: StateFlow<SuspendResult<Boolean>> = _removeArticleTakedStatus

    private val _substractArticleTakedCountStatus = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val substractArticleTakedCountStatus: StateFlow<SuspendResult<Boolean>> = _substractArticleTakedCountStatus

    private val _allArticlesTakedList = MutableStateFlow<SuspendResult<List<ArticleTaked>>>(SuspendResult.Loading)
    val allArticlesTakedList: StateFlow<SuspendResult<List<ArticleTaked>>> = _allArticlesTakedList

    private val _allArticlesReturnedList = MutableStateFlow<SuspendResult<List<ArticleReturn>>>(SuspendResult.Loading)
    val allArticlesReturnedList: StateFlow<SuspendResult<List<ArticleReturn>>> = _allArticlesReturnedList

    private val _isAdmin = MutableStateFlow<SuspendResult<Boolean>>(SuspendResult.Idle)
    val isAdmin: StateFlow<SuspendResult<Boolean>> = _isAdmin

    private val _allArticlesMovementsList = MutableStateFlow<SuspendResult<List<ArticleMovement>>>(SuspendResult.Loading)
    val allArticlesMovementsList: StateFlow<SuspendResult<List<ArticleMovement>>> = _allArticlesMovementsList

    private val _allUserMovementsList = MutableStateFlow<SuspendResult<List<ArticleMovement>>>(SuspendResult.Success(emptyList()))
    val allUserMovementsList: StateFlow<SuspendResult<List<ArticleMovement>>> =  _allUserMovementsList

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun insertUserInFirestore(user: User) {
        viewModelScope.launch {
            _insertUserStatus.value = SuspendResult.Loading
            _insertUserStatus.value = repository.insertUser(user)
        }
    }

    fun addArticleTaked(userId: String, articleTaked: ArticleTaked) {
        viewModelScope.launch {
            _addArticleTakedStatus.value = SuspendResult.Loading
            _addArticleTakedStatus.value = repository.addArticlesTaked(userId, articleTaked)
        }
    }

    fun addArticleReturn(userId: String, articleReturn: ArticleReturn) {
        viewModelScope.launch {
            _addArticleReturnStatus.value = SuspendResult.Loading
            _addArticleReturnStatus.value = repository.addArticleReturn(userId, articleReturn)
        }
    }

    fun getUserByEmail(email: String) {
        viewModelScope.launch {
            _userByEmail.value = SuspendResult.Loading
            _userByEmail.value = repository.getUserByEmail(email)
        }
    }

    fun getAllArticleTakedListById(userId: String) {
        viewModelScope.launch {
            _allArticlesTakedList.value = SuspendResult.Loading
            _allArticlesTakedList.value = repository.getArticleTakedList(userId)
        }
    }

    fun getAllArticleReturnedListById(userId: String) {
        viewModelScope.launch {
            _allArticlesReturnedList.value = SuspendResult.Loading
            _allArticlesReturnedList.value = repository.getArticleReturnedList(userId)
        }
    }

    fun getAllArticlesMovementsListById (isAdmin : Boolean,userId : String = ""){
        viewModelScope.launch {
            _allArticlesMovementsList.value = SuspendResult.Loading
            if(!isAdmin) _allArticlesMovementsList.value = repository.getAllArticleMovementById(userId)
            else _allArticlesMovementsList.value = repository.getAllUserArticleMovements()
        }
    }

    fun removeArticleTaked(userId: String, articleTakedId: String) {
        viewModelScope.launch {
            _removeArticleTakedStatus.value = SuspendResult.Loading
            _removeArticleTakedStatus.value = repository.removeArticleTaked(userId, articleTakedId)
        }
    }

    fun substractArticleTakedCount(userId: String, articleTakedId: String, subtractCount: Int) {
        viewModelScope.launch {
            _substractArticleTakedCountStatus.value = SuspendResult.Loading
            _substractArticleTakedCountStatus.value = repository.substractArticlesTakedCount(userId, subtractCount, articleTakedId)
        }
    }

    fun getAllUserMovementsList(){
        viewModelScope.launch {
            _allUserMovementsList.value = SuspendResult.Loading
            _allArticlesMovementsList.value = repository.getAllUserArticleMovements()
        }
    }

    fun getIfAdmin(userId: String) {
        viewModelScope.launch {
            _isAdmin.value = SuspendResult.Loading
            _isAdmin.value = repository.isAdmin(userId)
        }
    }
}
