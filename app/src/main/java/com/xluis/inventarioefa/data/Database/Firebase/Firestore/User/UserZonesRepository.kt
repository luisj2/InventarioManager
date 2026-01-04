package com.xluis.inventarioefa.data.Database.Firestore.User

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.data.Database.Firestore.FirestoreExceptionHandler
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserZones.UserZonesQuery
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_USERNAME_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_USER_ZONES_LIST_FIELD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserZonesRepository(private val fs: FirebaseFirestore) : UserZonesQuery,
    BaseFirestoreRepository(fs) {

    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)

    /**
     * Añade un ID de zona a la lista del usuario en Firestore.
     */
    override suspend fun addZoneId(zoneId: String, userId: String): SuspendResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                getUserCollection()
                    .document(userId)
                    .update(FIRESTORE_USER_ZONES_LIST_FIELD, FieldValue.arrayUnion(zoneId))
                    .await()
                SuspendResult.Success(true)
            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    /**
     * Obtiene la lista de IDs de zonas que tiene un usuario.
     */
    override suspend fun getZonesIdList(userId: String): SuspendResult<List<String>> =
        withContext(Dispatchers.IO) {
            try {
                val document = getUserCollection()
                    .document(userId)
                    .get()
                    .await()

                val zones = document.get(FIRESTORE_USER_ZONES_LIST_FIELD) as? List<String>

                return@withContext if (zones != null) {
                    SuspendResult.Success(zones)
                } else {
                    SuspendResult.Error("No se han podido cargar las zonas")
                }

            } catch (e: Exception) {
                SuspendResult.Error(FirestoreExceptionHandler.handle(e))
            }
        }

    override suspend fun getLoggedUsername(userId: String): SuspendResult<String> {
        return executeFirestoreOperation {
            val snapshot = getUserCollection()
                .document(userId)
                .get()
                .await()

            val userName = snapshot.getString(FIRESTORE_USER_USERNAME_FIELD)
                ?: throw Exception("userName field does not exist")

            userName
        }
    }


}
