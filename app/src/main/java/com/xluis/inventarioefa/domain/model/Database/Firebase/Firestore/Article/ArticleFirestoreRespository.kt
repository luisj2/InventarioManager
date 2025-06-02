package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Article

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.FirestoreExceptionHandler
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_ARTICLE_RETURN_BASE_ID_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_ARTICLE_RETURN_COUNT_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_ARTICLE_TAKED_BASE_ID_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_ARTICLE_TAKED_COUNT_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_COUNT
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_NAME
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_TAKED_ARTICLE_SUBCOLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_RETURN_ARTICLE_SUBCOLLECTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ArticleFirestoreRespository(private val fs: FirebaseFirestore) : ArticleFirestoreQuery {

    companion object {
        private const val ERROR = "ArticleFirestoreError"
    }

    private fun getArticleCollection(): CollectionReference =
        fs.collection(FIRESTORE_ARTICLE_COLLECTION)

    private fun getArticleTakedCollectionByArticleId(articleId: String) =
        getArticleCollection()
            .document(articleId)
            .collection(FIRESTORE_ARTICLE_TAKED_ARTICLE_SUBCOLLECTION)

    private fun getArticleReturnCollectionByArticleId(articleId: String) =
        getArticleCollection()
            .document(articleId)
            .collection(FIRESTORE_RETURN_ARTICLE_SUBCOLLECTION)


    override suspend fun insertArticle(article: Article): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!articleExist(article)) {
                    saveArticle(article)
                    SuspendResult.Success(true)
                } else {
                    SuspendResult.Success(false)
                }
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }


    override suspend fun substractArticleCount(
        articleId: String,
        substractCount: Int
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isValidCount(articleId, substractCount)) SuspendResult.Success(false)
                else {
                    substractCount(articleId, substractCount)
                    SuspendResult.Success(true)
                }
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    override suspend fun addArticleCount(articleId: String, addCount: Int): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isValidCount(articleId, addCount)) SuspendResult.Success(false)
                else {
                    if(isArticleExistById(articleId)){
                        addCount(articleId, addCount)
                    }
                    SuspendResult.Success(true)
                }
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    override suspend fun updateArticleById(
        articleId: String,
        article: Article
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                updateArticle(articleId, article)
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun updateArticle(articleId: String, article: Article) {
        getArticleCollection()
            .document(articleId)
            .set(article)
            .await()
    }

    private suspend fun addCount(articleId: String, addCount: Int) {
        val articleCurrentCount = getArticleCollection()
            .document(articleId)
            .get()
            .await()
            .toObject(Article::class.java)
            ?.count ?: 0

        val updateCount = articleCurrentCount + addCount

        getArticleCollection()
            .document(articleId)
            .update(mapOf(FIRESTORE_ARTICLE_COUNT to updateCount))
            .await()
    }

    private suspend fun substractCount(articleId: String, substractCount: Int) {
        val articleCurrentCount = getArticleCollection()
            .document(articleId)
            .get()
            .await()
            .toObject(Article::class.java)
            ?.count ?: 0

        val updateCount = articleCurrentCount - substractCount

        getArticleCollection()
            .document(articleId)
            .update(mapOf(FIRESTORE_ARTICLE_COUNT to updateCount))
            .await()
    }

    private suspend fun isValidCount(articleId: String, substractCount: Int): Boolean {
        val articleSnapshot = getArticleCollection()
            .document(articleId)
            .get()
            .await()

        val articleCount = articleSnapshot
            .toObject(Article::class.java)
            ?.count ?: -1

        return articleCount >= substractCount
    }

    private suspend fun articleExist(article: Article): Boolean {
        val result = getArticleCollection()
            .whereEqualTo(FIRESTORE_ARTICLE_NAME, article.name)
            .get()
            .await()
        return !result.isEmpty
    }

    private suspend fun isArticleExistById (articleId : String) : Boolean =
        getArticleCollection()
            .document(articleId)
            .get()
            .await()
            .exists()

    private suspend fun saveArticle(article: Article) {
        if (article.id == null) throw IllegalStateException("Ubicación ID debe ser distinto de null")

        getArticleCollection()
            .document(article.id!!)
            .set(article)
            .await()
    }

    override suspend fun deleteArticleById(id: String): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                removeArticleById(id)
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    override suspend fun getArticleTakedByBaseId(
        articleId: String,
        userId: String
    ): SuspendResult<List<ArticleTaked>> {
        return withContext(Dispatchers.IO) {
            try {
                val resultList = obtainArticleTakedListByBaseId(articleId, userId)
                SuspendResult.Success(resultList)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun obtainArticleTakedListByBaseId(
        articleId: String,
        userId: String
    ): List<ArticleTaked> {
        val baseId = "$userId-$articleId"
        return getArticleTakedCollectionByArticleId(articleId)
            .whereEqualTo(FIRESTORE_ARTICLE_ARTICLE_TAKED_BASE_ID_FIELD, baseId)
            .get()
            .await()
            .toObjects(ArticleTaked::class.java)

    }

    private suspend fun removeArticleById(id: String) {
        getArticleCollection()
            .document(id)
            .delete()
            .await()
    }

    override suspend fun removeArticleTakedById(
        articleId: String,
        articleTakedId: String
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                deleteArticleTaked(articleId, articleTakedId)
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun deleteArticleTaked(articleId: String, articleTakedId: String) {
        getArticleTakedCollectionByArticleId(articleId)
            .document(articleTakedId)
            .delete()
            .await()
    }

    override suspend fun getAllArticleTaked(articleId: String): SuspendResult<List<ArticleTaked>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = obtainArticleTakedList(articleId)
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun obtainArticleTakedList(
        articleId: String,
        userId: String = ""
    ): List<ArticleTaked> =
        getArticleTakedCollectionByArticleId(articleId)
            .whereEqualTo(FIRESTORE_ARTICLE_ARTICLE_TAKED_BASE_ID_FIELD, "$userId-$articleId")
            .get()
            .await()
            .toObjects(ArticleTaked::class.java)

    override suspend fun getAllArticleReturn(articleId: String): SuspendResult<List<ArticleReturn>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = obtainArticleReturnList(articleId,)
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun obtainArticleReturnList(
        articleId: String,
        userId: String = ""
    ): List<ArticleReturn> =

        getArticleReturnCollectionByArticleId(articleId)
            .whereEqualTo(FIRESTORE_ARTICLE_ARTICLE_RETURN_BASE_ID_FIELD, "$userId-$articleId")
            .get()
            .await()
            .toObjects(ArticleReturn::class.java)

    override suspend fun getAllArticlesMovementsByArticleId(
        articleId: String,
        userId: String
    ): SuspendResult<List<ArticleMovement>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = obtainArticleMovementsList(articleId, userId)
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun obtainArticleMovementsList(
        articleId: String,
        userId: String
    ): List<ArticleMovement> =
        obtainArticleReturnList(articleId, userId) + obtainArticleTakedList(articleId, userId)

    override suspend fun addArticleTaked(
        articleId: String,
        articleTaked: ArticleTaked
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val result = if (!isArticleTakedExist(articleId, articleTaked.userId,articleTaked.date)) {
                    saveArticleTaked(articleId, articleTaked)
                    true
                } else {
                    addArticleTakedCount(
                        articleId,
                        articleTaked.id ?: return@withContext SuspendResult.Success(false), // id inválido
                        articleTaked.articlesTakedCount
                    )
                }

                SuspendResult.Success(result)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }


    private suspend fun isArticleTakedExist(articleId: String, userId: String,date : String): Boolean {
        val id = "$userId-$articleId-$date"
         return getArticleTakedCollectionByArticleId(articleId)
            .document(id)
            .get()
            .await()
            .exists()
    }


    private suspend fun saveArticleTaked(articleId: String, articleTaked: ArticleTaked) {
        val id = articleTaked.id ?: throw Exception("ArticleTaked id no encontrado")

        getArticleTakedCollectionByArticleId(articleId)
            .document(id)
            .set(articleTaked)
            .await()
    }

    override suspend fun addArticleReturn(
        articleId: String,
        articleReturn: ArticleReturn
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isArticleReturnExist(
                        articleId,
                        articleReturn.id ?: throw Exception("Article returned id not found")
                    )
                ) {
                    saveReturnArticle(articleId, articleReturn)
                }else{
                    addArticleReturnCount(articleId,articleReturn.id ?: "",articleReturn.articlesReturnCount)
                }

                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun getCurrentArticleCount(articleId: String, articleTakedId: String): Int {
        val articleTaked = getArticleTakedCollectionByArticleId(articleId)
            .document(articleTakedId)
            .get()
            .await()
            .toObject(ArticleTaked::class.java)


        return articleTaked?.articlesTakedCount ?: -1
    }

    override suspend fun addArticleTakedCount(
        articleId: String,
        articleTakedId: String,
        articleTakdCount: Int
    ): Boolean {
            return withContext(Dispatchers.IO) {
                try {
                    val currentCount = getCurrentArticleCount(articleId, articleTakedId)

                        val updatedValue = currentCount + articleTakdCount
                        getArticleTakedCollectionByArticleId(articleId)
                            .document(articleTakedId)
                            .update(mapOf(FIRESTORE_ARTICLE_ARTICLE_TAKED_COUNT_FIELD to updatedValue))
                            .await()
                        true

                } catch (e: Exception) {
                    false
                }
            }
    }

    override suspend fun addArticleReturnCount(articleId : String,articleReturnId: String, articleReturnCount: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val currentCount = getCurrentArticleReturnCount(articleId, articleReturnId)

                val updateValue = currentCount + articleReturnCount

                getArticleReturnCollectionByArticleId(articleId)
                    .document(articleReturnId)
                    .update(mapOf(FIRESTORE_ARTICLE_ARTICLE_RETURN_COUNT_FIELD to updateValue))
                    .await()

                true
            } catch (e: Exception) {
                false
            }
        }
    }

    private suspend fun getCurrentArticleReturnCount(
        artcleId: String,
        articleReturnId: String
    ): Int =
        getArticleReturnCollectionByArticleId(artcleId)
            .document(articleReturnId)
            .get()
            .await()
            .toObject(ArticleTaked::class.java)
            ?.articlesTakedCount ?: 0

    private suspend fun isArticleReturnExist(articleId: String, articleReturnId: String): Boolean =
        getArticleReturnCollectionByArticleId(articleId)
            .document(articleReturnId)
            .get()
            .await()
            .exists()


    private suspend fun saveReturnArticle(articleId: String, articleReturn: ArticleReturn) {
        val id = articleReturn.id ?: throw Exception("ArticleReturn id no encontrado")

        getArticleReturnCollectionByArticleId(articleId)
            .document(id)
            .set(articleReturn)
            .await()
    }

    override suspend fun getAllArticles(): SuspendResult<List<Article>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = getFirestoreAllArticles()
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    override suspend fun getArticleById(articleId: String): SuspendResult<Article?> {
        return withContext(Dispatchers.IO) {
            try {
                val article = getFirestoreArticleById(articleId)
                SuspendResult.Success(article)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }


    private suspend fun getFirestoreArticleById(articleId: String): Article? {
        val result = getArticleCollection()
            .document(articleId)
            .get()
            .await()
        return result.toObject(Article::class.java)
    }

    private suspend fun getFirestoreAllArticles(): List<Article> {
        val result = getArticleCollection()
            .get()
            .await()
        return result.documents.mapNotNull { it.toObject(Article::class.java) }
    }
}


