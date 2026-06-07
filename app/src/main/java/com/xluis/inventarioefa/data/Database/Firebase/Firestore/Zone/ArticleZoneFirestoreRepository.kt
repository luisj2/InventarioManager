package com.xluis.inventarioefa.data.Database.Firestore.Zone

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.Zone.ArticleZoneFirestoreQuery
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Model.Firestore.Article.ArticleFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_COUNT
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_DESCRIPTION_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_ARTICLE_SUBCOLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_COLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ArticleZoneFirestoreRepository(
    private val fs: FirebaseFirestore
) : ArticleZoneFirestoreQuery, BaseFirestoreRepository(fs) {

    // ============================================================
    // 🔹 COLECCIONES Y REFERENCIAS PÚBLICAS
    // ============================================================

    /** Colección de artículos de una zona */
    fun getArticleCollection(zoneId: String): CollectionReference =
        fs.collection(FIRESTORE_ZONES_COLLECTION)
            .document(zoneId)
            .collection(FIRESTORE_ZONES_ARTICLE_SUBCOLLECTION)



    /** Referencia a un artículo por ID */
    fun getArticleDocumentRef(zoneId: String, articleId: String?): DocumentReference {
        val id = articleId ?: throw IllegalStateException("El ID del artículo no puede ser nulo")
        return getArticleCollection(zoneId).document(id)
    }

    /** Lista de pares (DocumentReference, ArticleFirestore) para batch */
    fun buildArticleReferencesList(
        zoneId: String,
        articleList: List<ArticleFirestore>
    ): List<Pair<DocumentReference, ArticleFirestore>> {
        val collection = getArticleCollection(zoneId)
        return articleList.mapNotNull { article ->
            article.id?.let { id ->
                val docRef = collection.document(id)
                docRef to article
            }
        }
    }



    override suspend fun insertArticle(
        zoneId: String,
        articleFirestore: ArticleFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getArticleDocumentRef(zoneId, articleFirestore.id).set(articleFirestore).await()
            true
        }
    }

    override suspend fun insertArticleList(
        zoneId: String,
        articleList: List<ArticleFirestore>
    ): SuspendResult<Boolean> {
        val refs = buildArticleReferencesList(zoneId, articleList)
        return insertListInBatch(refs)
    }

    override suspend fun updateArticleDescription(
        zoneId: String,
        articleId: String,
        oldDescription: String,
        newDescription: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {

            val articleRef = getArticleDocumentRef(zoneId, articleId)

            fs.runTransaction { transaction ->

                val snapshot = transaction.get(articleRef)
                val article = snapshot.toObject(ArticleFirestore::class.java)
                    ?: return@runTransaction false

                val updatedDescriptions = article.descriptions.map {
                    if (it == oldDescription) newDescription else it
                }

                transaction.update(
                    articleRef,
                    FIRESTORE_ARTICLE_DESCRIPTION_FIELD,
                    updatedDescriptions
                )

                true
            }.await()
        }
    }

    // ============================================================
    // ✅ OBTENER
    // ============================================================

    override suspend fun getArticleListByZoneId(
        zoneId: String
    ): SuspendResult<List<ArticleFirestore>> {
        return executeFirestoreOperation {
            getArticleCollection(zoneId).get().await().toObjects(ArticleFirestore::class.java)
        }
    }

    override fun getArticleListByZoneIdFlow(zoneId: String): Flow<List<ArticleFirestore>> = callbackFlow {

        val listener = getArticleCollection(zoneId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val articles = snapshot
                    ?.toObjects(ArticleFirestore::class.java)
                    ?: emptyList()

                trySend(articles)
            }

        awaitClose { listener.remove() }
    }
    override suspend fun getArticleById(
        zoneId: String,
        articleId: String
    ): SuspendResult<ArticleFirestore?> {
        return executeFirestoreOperation {
            getArticleDocumentRef(zoneId, articleId).get().await()
                .toObject(ArticleFirestore::class.java)
        }
    }

    override suspend fun haveZoneArticleById(
        zoneId: String,
        articleId: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val doc = getArticleDocumentRef(zoneId, articleId).get().await()
            doc.exists()
        }
    }




    override suspend fun substractArticleCount(
        zoneId: String,
        articleId: String,
        substractCount: Int
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val articleRef = getArticleDocumentRef(zoneId, articleId)
            val articleFirestore = articleRef.get().await().toObject(ArticleFirestore::class.java)
            val currentCount = articleFirestore?.count ?: 0

            if (currentCount < substractCount) return@executeFirestoreOperation false
            articleRef.update(FIRESTORE_ARTICLE_COUNT, currentCount - substractCount).await()
            true
        }
    }

    override suspend fun addArticleCount(
        zoneId: String,
        articleId: String,
        addCount: Int
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val articleRef = getArticleDocumentRef(zoneId, articleId)
            val articleFirestore = articleRef.get().await().toObject(ArticleFirestore::class.java)
            val currentCount = articleFirestore?.count ?: 0

            articleRef.update(FIRESTORE_ARTICLE_COUNT, currentCount + addCount).await()
            true
        }
    }

    override suspend fun updateArticleById(
        zoneId: String,
        articleId: String,
        articleFirestore: ArticleFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getArticleDocumentRef(zoneId, articleId).set(articleFirestore).await()
            true
        }
    }




    override suspend fun moveArticle(
        articleToMove: ArticleFirestore,
        zoneIdToRemove: String,
        zoneIdToAdd: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            fs.runTransaction { transaction ->
                if (articleToMove.id != null) {
                    val removeRef = getArticleDocumentRef(zoneIdToRemove, articleToMove.id)
                    val insertRef = getArticleDocumentRef(zoneIdToAdd, articleToMove.id)
                    transaction.delete(removeRef)
                    transaction.set(insertRef, articleToMove)
                }
            }.await()
            true
        }
    }



    override suspend fun deleteArticleById(
        zoneId: String,
        articleId: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getArticleDocumentRef(zoneId, articleId).delete().await()
            true
        }
    }

    override suspend fun deleteArticlesByZoneId(zoneId: String): SuspendResult<Boolean> {
        return executeBatchOperation { batch ->
            val snapshot = getArticleCollection(zoneId).get().await()
            snapshot.documents.forEach { doc ->
                batch.delete(doc.reference)
            }
        }
    }

    override suspend fun removeArticleListByIdList(
        zoneId: String,
        idList: List<String>
    ): SuspendResult<Boolean> {
        if (idList.isEmpty()) {
            return SuspendResult.Error("La lista de artículos está vacía")
        }

        return executeBatchOperation { batch ->
            val collectionRef = getArticleCollection(zoneId)
            idList.forEach { id ->
                val docRef = collectionRef.document(id)
                batch.delete(docRef)
            }
        }
    }



    override suspend fun removeArticleCount(
        zoneId: String,
        articleId: String,
        countToRemove: Int
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val articleRef = getArticleDocumentRef(zoneId, articleId)

            fs.runTransaction { transaction ->
                val snapshot = transaction.get(articleRef)
                val article = snapshot.toObject(ArticleFirestore::class.java)
                    ?: return@runTransaction false // artículo no existe

                val currentCount = article.count

                if (currentCount < countToRemove) {
                    return@runTransaction false // no hay suficiente cantidad
                } else if (currentCount == countToRemove) {
                    transaction.delete(articleRef)
                } else {
                    transaction.update(articleRef, FIRESTORE_ARTICLE_COUNT, currentCount - countToRemove)
                }

                true
            }.await()
        }
    }
    suspend fun upsertArticleList(
        zoneId: String,
        articles: List<ArticleFirestore>
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            fs.runTransaction { transaction ->
                articles.forEach { article ->
                    val articleRef = getArticleDocumentRef(zoneId, article.id)
                    val snapshot = transaction.get(articleRef)
                    val existingArticle = snapshot.toObject(ArticleFirestore::class.java)

                    if (existingArticle == null) {
                        // Si no existe, insertamos el artículo tal cual
                        transaction.set(articleRef, article)
                    } else {
                        transaction.update(articleRef, FIRESTORE_ARTICLE_COUNT, article.count)
                    }
                }
                true
            }.await()
        }
    }



    override suspend fun addArticleCount(
        zoneId: String,
        article: ArticleFirestore,
        countToAdd: Int
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val articleRef = getArticleDocumentRef(zoneId, article.id)

            fs.runTransaction { transaction ->
                val snapshot = transaction.get(articleRef)
                val existingArticle = snapshot.toObject(ArticleFirestore::class.java)

                if (existingArticle == null) {
                    // Si no existe, lo insertamos con la cantidad inicial
                    val newArticle = article.copy(count = countToAdd)
                    transaction.set(articleRef, newArticle)
                } else {
                    // Si existe, sumamos la cantidad
                    val newCount = existingArticle.count + countToAdd
                    transaction.update(articleRef, FIRESTORE_ARTICLE_COUNT, newCount)
                }

                true
            }.await()
        }
    }

    override suspend fun changeArticleCount(
        zoneId: String,
        articleId: String,
        newCount: Int
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val articleRef = getArticleDocumentRef(zoneId, articleId)

            fs.runTransaction { transaction ->
                val snapshot = transaction.get(articleRef)

                if (!snapshot.exists()) {
                    return@runTransaction false
                }

                if (newCount <= 0) {
                    transaction.delete(articleRef)
                } else {
                    transaction.update(
                        articleRef,
                        FIRESTORE_ARTICLE_COUNT,
                        newCount
                    )
                }

                true
            }.await()
        }
    }

}
