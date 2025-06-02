package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User

import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.User.User

interface UserFirestoreQuery {
    // INSERT
    suspend fun insertUser(user: User): SuspendResult<Boolean>

    // SET
    suspend fun addArticlesTaked(userId: String, articleTaked: ArticleTaked): SuspendResult<Boolean>

    suspend fun addArticleReturn(userId: String, articleReturn: ArticleReturn): SuspendResult<Boolean>

    suspend fun substractArticlesTakedCount(userId: String, submitCount: Int, articleTakedId: String): SuspendResult<Boolean>

    suspend fun addArticleTakedCount(userId : String,articleTakedId : String,articleTakedCount : Int) : SuspendResult<Boolean>
    suspend fun addArticleReturnCount (userId : String,articleReturnId : String,articleReturnCount : Int) : SuspendResult<Boolean>

    // REMOVE
    suspend fun removeArticleTaked(userId: String, articleTakedId: String): SuspendResult<Boolean>

    // GET
    suspend fun getArticleTakedList(userId: String): SuspendResult<List<ArticleTaked>>

    suspend fun getArticleReturnedList(userId: String): SuspendResult<List<ArticleReturn>>

    suspend fun getAllArticleMovementById(userId: String): SuspendResult<List<ArticleMovement>>

    suspend fun getAllUserArticleMovements(): SuspendResult<List<ArticleMovement>>

    suspend fun getUserByEmail(email: String): SuspendResult<User?>

    suspend fun isAdmin(userId: String): SuspendResult<Boolean>
}
