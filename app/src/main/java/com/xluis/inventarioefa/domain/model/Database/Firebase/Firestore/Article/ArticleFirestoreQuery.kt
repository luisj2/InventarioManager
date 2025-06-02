package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Article

import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface ArticleFirestoreQuery {
    // INSERT
    suspend fun insertArticle(article: Article): SuspendResult<Boolean>

    // SET
    suspend fun substractArticleCount(articleId: String, substractCount: Int): SuspendResult<Boolean>

    suspend fun addArticleCount(articleId: String, addCount: Int): SuspendResult<Boolean>

    suspend fun updateArticleById(articleId: String, article: Article): SuspendResult<Boolean>

    // DELETE
    suspend fun deleteArticleById(id: String): SuspendResult<Boolean>

    suspend fun getArticleTakedByBaseId (articleId : String,userId : String) : SuspendResult<List<ArticleTaked>>

    suspend fun removeArticleTakedById(articleId: String, articleTakedId: String): SuspendResult<Boolean>

    suspend fun addArticleTaked(articleId: String, articleTaked: ArticleTaked): SuspendResult<Boolean>

    suspend fun addArticleReturn(articleId: String, articleReturn: ArticleReturn): SuspendResult<Boolean>

    suspend fun addArticleTakedCount (articleId : String,articleTakedId : String,articleTakdCount : Int) : Boolean

    suspend fun addArticleReturnCount (articleId : String,articleTakedId : String,articleReturnCount : Int) : Boolean

    // GET
    suspend fun getAllArticleTaked(articleId: String): SuspendResult<List<ArticleTaked>>

    suspend fun getAllArticleReturn(articleId: String): SuspendResult<List<ArticleReturn>>

    suspend fun getAllArticlesMovementsByArticleId(articleId: String,userId : String): SuspendResult<List<ArticleMovement>>

    suspend fun getAllArticles(): SuspendResult<List<Article>>

    suspend fun getArticleById(articleId: String): SuspendResult<Article?>
}
