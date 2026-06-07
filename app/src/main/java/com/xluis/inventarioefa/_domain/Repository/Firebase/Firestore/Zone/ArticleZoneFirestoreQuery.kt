package com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.Zone

import com.xluis.inventarioefa.data.Model.Firestore.Article.ArticleFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.flow.Flow

interface ArticleZoneFirestoreQuery {

    // INSERT
    suspend fun insertArticle(
        zoneId: String,
        articleFirestore: ArticleFirestore
    ): SuspendResult<Boolean>

    suspend fun insertArticleList(
        zoneId: String,
        articleList: List<ArticleFirestore>
    ): SuspendResult<Boolean>

    suspend fun updateArticleDescription(
        zoneId: String,
        articleId: String,
        oldDescription: String,
        newDescription: String
    ): SuspendResult<Boolean>


    // GET
    suspend fun getArticleListByZoneId(zoneId: String): SuspendResult<List<ArticleFirestore>>

    fun getArticleListByZoneIdFlow (zoneId : String) : Flow<List<ArticleFirestore>>

    suspend fun getArticleById(zoneId: String, articleId: String): SuspendResult<ArticleFirestore?>

    suspend fun haveZoneArticleById(zoneId: String, articleId: String): SuspendResult<Boolean>

    // UPDATE
    suspend fun substractArticleCount(
        zoneId: String,
        articleId: String,
        substractCount: Int
    ): SuspendResult<Boolean>

    suspend fun addArticleCount(
        zoneId: String,
        articleId: String,
        addCount: Int
    ): SuspendResult<Boolean>

    suspend fun changeArticleCount(
        zoneId : String,
        articleId : String,
        newCount : Int
    ) : SuspendResult<Boolean>

    suspend fun updateArticleById(
        zoneId: String,
        articleId: String,
        articleFirestore: ArticleFirestore
    ): SuspendResult<Boolean>


    //UPDATE

    suspend fun moveArticle(
        articleToMove: ArticleFirestore,
        zoneIdToremove: String,
        zoneIdToAdd: String
    ): SuspendResult<Boolean>

    // REMOVE
    suspend fun deleteArticleById(zoneId: String, articleId: String): SuspendResult<Boolean>

    suspend fun deleteArticlesByZoneId(zoneId: String): SuspendResult<Boolean>

    suspend fun removeArticleListByIdList (zoneId : String, idList : List<String>) :SuspendResult<Boolean>

    suspend fun removeArticleCount(
        zoneId: String,
        articleId: String,
        countToRemove: Int
    ) : SuspendResult<Boolean>

    suspend fun addArticleCount(
        zoneId: String,
        article: ArticleFirestore,
        countToAdd: Int
    ) : SuspendResult<Boolean>
}
