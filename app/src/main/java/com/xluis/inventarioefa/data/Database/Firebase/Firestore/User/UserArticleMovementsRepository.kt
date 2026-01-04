package com.xluis.inventarioefa.data.Database.Firestore.User

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserMovements.UserArticleMovementQuery
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_MOVEMENTS_LIST_FIELD
import kotlinx.coroutines.tasks.await

class UserArticleMovementsRepository(private val fs: FirebaseFirestore) :
    UserArticleMovementQuery, BaseFirestoreRepository(fs){

    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)


    // ----------------------------- MOVIMIENTOS COMBINADOS -----------------------------

    override suspend fun getAllArticleMovementsIdByUserId(userId: String): SuspendResult<List<String>> =
        executeFirestoreOperation {
            obtainArticleMovementsIdByUserId(userId)
        }

    private suspend fun obtainArticleMovementsIdByUserId(userId: String): List<String> {
        val document = getUserCollection()
            .document(userId)
            .get()
            .await()

        if (!document.exists()) throw NoSuchElementException("El usuario no existe en la base de datos")

        return document.get(FIRESTORE_USER_MOVEMENTS_LIST_FIELD) as? List<String> ?: emptyList()
    }


}