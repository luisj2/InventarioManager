package com.xluis.inventarioefa.data.Database.Firestore.Zone

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.Zone.ArticleMovementQuery
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.utils.FIRESTORE_MOVEMENTS_ZONEID_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_MOVEMENTS_SUBCOLLECTION
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ArticleMovementRepository(
    private val fs: FirebaseFirestore
) : ArticleMovementQuery, BaseFirestoreRepository(fs) {

    private fun getMovementsCollection(zoneId: String): CollectionReference =
        fs.collection(FIRESTORE_ZONES_COLLECTION)
            .document(zoneId)
            .collection(FIRESTORE_ZONES_MOVEMENTS_SUBCOLLECTION)


    override suspend fun insertMovement(
        zoneId: String,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getMovementDocumentRef(zoneId, movement.id).set(movement).await()
            true
        }
    }



    override suspend fun insertMovementsList(
        zoneId: String,
        movementsList: List<ArticleMovementFirestore>
    ): SuspendResult<Boolean> {
        val refs = buildMovementReferencesList(zoneId, movementsList)
        return insertListInBatch(refs)
    }


    override suspend fun getMovementsListByZoneId(
        zoneId: String
    ): SuspendResult<List<ArticleMovementFirestore>> {
        return executeFirestoreOperation {
            val snapshot = getMovementsCollection(zoneId).get().await()
            snapshot.toObjects(ArticleMovementFirestore::class.java)
        }
    }

    override fun getMovementListByZoneIdFlow(zoneId: String): Flow<List<ArticleMovementFirestore>> = callbackFlow {

        val listener = getMovementsCollection(zoneId)
            .addSnapshotListener { snapshot, error ->

                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val movements = snapshot
                    ?.toObjects(ArticleMovementFirestore::class.java)
                    ?: emptyList()

                trySend(movements).isSuccess
            }

        awaitClose { listener.remove() }
    }

    override suspend fun deleteMovementsByZoneId(zoneId: String): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            // Referencia a la colección de movimientos
            val movementsCollection =  getMovementsCollection(zoneId)

            val querySnapshot = movementsCollection
                .whereEqualTo(FIRESTORE_MOVEMENTS_ZONEID_FIELD, zoneId)
                .get()
                .await()

            querySnapshot.documents.forEach { doc ->
                movementsCollection.document(doc.id).delete().await()
            }

            true
        }
    }



    //REFERENCES
    fun getMovementDocumentRef(
        zoneId: String,
        movementId : String?
    ): DocumentReference {
        val id =
            movementId ?: throw IllegalStateException("El ID del movimiento no puede ser nulo")
        return getMovementsCollection(zoneId).document(id)
    }

    fun buildMovementReferencesList(
        zoneId: String,
        movementsList: List<ArticleMovementFirestore>
    ): List<Pair<DocumentReference, ArticleMovementFirestore>> {
        val collection = getMovementsCollection(zoneId)

        return movementsList.mapNotNull { movement ->
            movement.id?.let { id ->
                val docRef = collection.document(id)
                docRef to movement
            }
        }
    }


}
