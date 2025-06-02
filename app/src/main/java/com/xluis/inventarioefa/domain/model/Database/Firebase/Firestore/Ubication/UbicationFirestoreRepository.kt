package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Ubication
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.utils.FIRESTORE_UBICATION_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_UBICATION_NAME_FIELD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UbicationFirestoreRepository(private val fs: FirebaseFirestore) : UbicationFirestoreQuery {

    companion object {
        private const val ERROR = "UbicationFirestoreError"
    }

    private fun getUbicationCollection(): CollectionReference =
        fs.collection(FIRESTORE_UBICATION_COLLECTION)

    override suspend fun insertUbication(ubication: Ubication): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                if (!ubicationExist(ubication)) {
                    saveUbication(ubication)
                }
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun ubicationExist(ubication: Ubication): Boolean {
        val result = getUbicationCollection()
            .whereEqualTo(FIRESTORE_UBICATION_NAME_FIELD, ubication.name)
            .get()
            .await()
        return !result.isEmpty
    }

    private suspend fun saveUbication(ubication: Ubication) {
        if (ubication.id == null) throw IllegalStateException("Ubication ID must not be null")

        getUbicationCollection()
            .document(ubication.id!!)
            .set(ubication)
            .await()
    }

    override suspend fun deleteUbicationById(id: String): SuspendResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                removeUbication(id)
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun removeUbication(id: String) {
        getUbicationCollection()
            .document(id)
            .delete()
            .await()
    }

    override suspend fun getAllUbications(): SuspendResult<List<Ubication>> {
        return withContext(Dispatchers.IO) {
            try {
                val list = getFirestoreAllUbications()
                SuspendResult.Success(list)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    private suspend fun getFirestoreAllUbications(): List<Ubication> {
        val result = getUbicationCollection()
            .get()
            .await()
        return result.documents.mapNotNull { it.toObject(Ubication::class.java) }
    }
}
