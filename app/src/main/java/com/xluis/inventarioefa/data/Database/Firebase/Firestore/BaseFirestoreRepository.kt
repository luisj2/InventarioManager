package com.xluis.inventarioefa.data.Database.Firebase.Firestore

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.xluis.inventarioefa.data.Database.Firestore.FirestoreExceptionHandler
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

abstract class BaseFirestoreRepository (private val fs : FirebaseFirestore) {
    protected suspend fun <T> executeFirestoreOperation(block: suspend () -> T): SuspendResult<T> {
        return withContext(Dispatchers.IO) {
            try {
                SuspendResult.Success(block())
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }
    }

    protected suspend fun executeBatchOperation(
        buildBatch: suspend (batch: WriteBatch) -> Unit
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val batch = fs.batch()
            buildBatch(batch)
            batch.commit().await()
            true
        }
    }

    protected suspend fun <T : Any> insertListInBatch(
        pairs: List<Pair<DocumentReference, T>>
    ): SuspendResult<Boolean> {
        return executeBatchOperation { batch ->
            pairs.forEach { (ref, data) -> batch.set(ref, data) }
        }
    }

}