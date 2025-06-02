package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.User.Rol
import com.xluis.inventarioefa.domain.model.DataClass.User.User
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.FirestoreExceptionHandler
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_ARTICLE_RETURN_COUNT_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_ARTICLE_TAKED_COUNT_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_RETURN_ARTICLE_SUBCOLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_TAKED_ARTICLE_COUNT_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_USER_TAKED_ARTICLE_SUBCOLLECTION
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserFirestoreRepository(private val fs: FirebaseFirestore) : UserFirestoreQuery {

    companion object {
        private const val ERROR = "UserFirestoreError"
    }

    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)
    private fun getUserArticlesTakedCollectionByUserId(userId: String) =
        getUserCollection().document(userId).collection(FIRESTORE_USER_TAKED_ARTICLE_SUBCOLLECTION)

    private fun getUserArticlesReturnCollectionByUserId(userId: String) =
        getUserCollection().document(userId).collection(FIRESTORE_USER_RETURN_ARTICLE_SUBCOLLECTION)

    private suspend fun getArticlesTakedCount(userId: String, articleTakedId: String): Int =
        getUserArticlesTakedCollectionByUserId(userId)
            .document(articleTakedId)
            .get()
            .await()
            .toObject(ArticleTaked::class.java)?.articlesTakedCount
            ?: throw Exception("ArticleTaked id not found")

    override suspend fun insertUser(user: User): SuspendResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                saveUser(user)
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    override suspend fun addArticlesTaked(
        userId: String,
        articleTaked: ArticleTaked
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val result =
                    if (!isArticleTakedExist(articleTaked.articleId, userId, articleTaked.date)) {
                        saveArticleTakedSubcollection(userId, articleTaked)
                        true
                    } else {
                        addArticleTakedCount(
                            userId,
                            articleTaked.id ?: "",
                            articleTaked.articlesTakedCount
                        )
                        false
                    }
                SuspendResult.Success(result)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun isArticleTakedExist(
        articleId: String,
        userId: String,
        date: String
    ): Boolean {
        val id = "$userId-$articleId-$date"

        return getUserArticlesTakedCollectionByUserId(userId)
            .document(id)
            .get()
            .await()
            .exists()
    }

    override suspend fun addArticleReturn(
        userId: String,
        articleReturn: ArticleReturn
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!isArticleReturnExist(articleReturn.id ?: "", userId, articleReturn.date))
                    saveArticleReturnSubcollection(userId, articleReturn)
                else {
                    addArticleReturnCount(
                        userId,
                        articleReturn.id ?: "",
                        articleReturn.articlesReturnCount
                    )
                }
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }


    private suspend fun isArticleReturnExist(
        articleId: String,
        userId: String,
        date: String
    ): Boolean {
        val id = "$userId-$articleId-$date"

        return getUserArticlesReturnCollectionByUserId(userId)
            .document(id)
            .get()
            .await()
            .exists()
    }

    override suspend fun removeArticleTaked(
        userId: String,
        articleTakedId: String
    ): SuspendResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            removeArticleTakedByUserAndArticleId(userId, articleTakedId)
            SuspendResult.Success(true)
        } catch (e: Exception) {
            SuspendResult.Error(FirestoreExceptionHandler.handle(e))
        }
    }

    private suspend fun removeArticleTakedByUserAndArticleId(
        userId: String,
        articleTakedId: String
    ) {
        getUserArticlesTakedCollectionByUserId(userId).document(articleTakedId).delete().await()
    }

    override suspend fun getArticleTakedList(userId: String): SuspendResult<List<ArticleTaked>> =
        withContext(Dispatchers.IO) {
            try {
                val list = obtainArticleTakedListByUserId(userId)
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    private suspend fun obtainArticleTakedListByUserId(userId: String): List<ArticleTaked> =
        getUserArticlesTakedCollectionByUserId(userId).get()
            .await().documents.mapNotNull { it.toObject(ArticleTaked::class.java) }

    override suspend fun getArticleReturnedList(userId: String): SuspendResult<List<ArticleReturn>> =
        withContext(Dispatchers.IO) {
            try {
                val list = obtainArticleReturnedListById(userId)
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    override suspend fun getAllArticleMovementById(userId: String): SuspendResult<List<ArticleMovement>> =
        withContext(Dispatchers.IO) {
            try {
                val list = obtainArticlesMovementsById(userId)
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    private suspend fun obtainArticlesMovementsById(userId: String): List<ArticleMovement> =
        obtainArticleTakedListByUserId(userId) + obtainArticleReturnedListById(userId)

    override suspend fun getAllUserArticleMovements(): SuspendResult<List<ArticleMovement>> =
        withContext(Dispatchers.IO) {
            try {
                val list = obtainAllUsersArticleMovements()
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }


    private suspend fun obtainAllUsersArticleMovements(): List<ArticleMovement> {
        val result = mutableListOf<ArticleMovement>()
        val userSnapshot = getUserCollection().get().await()

        for (userDoc in userSnapshot.documents) {
            val userId = userDoc.id
            val articlesTakedList =
                getUserArticlesTakedCollectionByUserId(userId).get().await().documents.mapNotNull {
                    it.toObject(ArticleTaked::class.java)?.copy(id = it.id)
                }
            val articlesReturnList =
                getUserArticlesReturnCollectionByUserId(userId).get().await().documents.mapNotNull {
                    it.toObject(ArticleReturn::class.java)?.copy(id = it.id)
                }
            result.addAll(articlesTakedList)
            result.addAll(articlesReturnList)
        }
        return result
    }

    private suspend fun obtainArticleReturnedListById(userId: String): List<ArticleReturn> =
        getUserArticlesReturnCollectionByUserId(userId).get()
            .await().documents.mapNotNull { it.toObject(ArticleReturn::class.java) }

    private suspend fun saveArticleReturnSubcollection(
        userId: String,
        articleReturn: ArticleReturn
    ) {
        val articleId = articleReturn.id ?: throw Exception("Article id not find")
        getUserArticlesReturnCollectionByUserId(userId).document(articleId).set(articleReturn)
            .await()
    }

    override suspend fun substractArticlesTakedCount(
        userId: String,
        submitCount: Int,
        articleTakedId: String
    ): SuspendResult<Boolean> = withContext(Dispatchers.IO) {
        try {
            val currentCount = getArticlesTakedCount(userId, articleTakedId)
            if (currentCount < submitCount) {
                SuspendResult.Error("Cantidad no valida")
            } else {
                submitArticlesCount(userId, submitCount, articleTakedId, currentCount)
                SuspendResult.Success(true)
            }
        } catch (e: Exception) {
            SuspendResult.Error(FirestoreExceptionHandler.handle(e))
        }
    }

    override suspend fun addArticleTakedCount(
        userId: String,
        articleTakedId: String,
        articleTakedCount: Int
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentCount = getCurrentArticleTakedCount(userId, articleTakedId)

                val updatedValue = currentCount + articleTakedCount

                getUserArticlesTakedCollectionByUserId(userId)
                    .document(articleTakedId)
                    .update(mapOf(FIRESTORE_ARTICLE_ARTICLE_TAKED_COUNT_FIELD to updatedValue))
                    .await()
                SuspendResult.Success(true)

            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun getCurrentArticleTakedCount(
        userId: String,
        articleTAkedId: String
    ): Int {
        return getUserArticlesTakedCollectionByUserId(userId)
            .document(articleTAkedId)
            .get()
            .await()
            .toObject(ArticleTaked::class.java)
            ?.articlesTakedCount ?: 0

    }

    override suspend fun addArticleReturnCount(
        userId: String,
        articleReturnId: String,
        articleReturnCount: Int
    ): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val currentCount = getCurrentArticleReturnCount(userId, articleReturnId)

                val updateValue = currentCount + articleReturnCount

                getUserArticlesTakedCollectionByUserId(userId)
                    .document(articleReturnId)
                    .update(mapOf(FIRESTORE_ARTICLE_ARTICLE_RETURN_COUNT_FIELD to updateValue))
                    .await()

                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun getCurrentArticleReturnCount(
        userId: String,
        articleReturnId: String
    ): Int =
        getUserArticlesReturnCollectionByUserId(userId)
            .document(articleReturnId)
            .get()
            .await()
            .toObject(ArticleTaked::class.java)
            ?.articlesTakedCount ?: 0


    private suspend fun submitArticlesCount(
        userId: String,
        submitCount: Int,
        articleTakedId: String,
        currentCount: Int
    ) {
        val updateCount = currentCount - submitCount
        getUserArticlesTakedCollectionByUserId(userId).document(articleTakedId).update(
            mapOf(FIRESTORE_USER_TAKED_ARTICLE_COUNT_FIELD to updateCount)
        ).await()
    }


    private suspend fun saveArticleTakedSubcollection(userId: String, articleTaked: ArticleTaked) {
        val articleId = articleTaked.id ?: throw Exception("Articl Taked  id not find")
        getUserCollection().document(userId).collection(FIRESTORE_USER_TAKED_ARTICLE_SUBCOLLECTION)
            .document(articleId).set(articleTaked).await()
    }

    private suspend fun saveUser(user: User) {
        getUserCollection().document(user.email).set(user).await()
    }

    override suspend fun getUserByEmail(email: String): SuspendResult<User?> =
        withContext(Dispatchers.IO) {
            try {
                val user = getUserByEmailDocumentId(email)
                SuspendResult.Success(user)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    override suspend fun isAdmin(userId: String): SuspendResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                when (val userResult = getUserByEmail(userId)) {
                    is SuspendResult.Success -> SuspendResult.Success(userResult.data?.rol == Rol.ADMIN.displayName)
                    is SuspendResult.Error -> SuspendResult.Error(userResult.message)
                    is SuspendResult.Loading -> SuspendResult.Loading
                    else -> SuspendResult.Idle
                }
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    private suspend fun getUserByEmailDocumentId(email: String): User? =
        getUserCollection().document(email).get().await().toObject(User::class.java)
}



