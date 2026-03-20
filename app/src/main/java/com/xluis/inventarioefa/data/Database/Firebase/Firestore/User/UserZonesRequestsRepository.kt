package com.xluis.inventarioefa.data.Database.Firebase.Firestore.User

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.User.UserZonesRequestsQuery
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Model.Firestore.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_REQUESTS_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_MEMEBERS_FIELD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserZonesRequestsRepository(
    private val fs: FirebaseFirestore
) : BaseFirestoreRepository(fs), UserZonesRequestsQuery {

    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)

    private fun getUserRequestsCollection(userId: String) =
        getUserCollection()
            .document(userId)
            .collection(FIRESTORE_USER_REQUESTS_FIELD)

    // 🔹 Insertar solicitud de usuario
    private suspend fun insertUserRequest(
        newRequest: ZoneRequestFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getUserRequestsCollection(newRequest.receiverId)
                .document(newRequest.id)
                .set(newRequest)
                .await()
            true
        }
    }

    // 🔹 Enviar solicitud solo si no es miembro ni hay solicitud pendiente
    override suspend fun sendZoneRequestIfNeeded(request: ZoneRequestFirestore): SuspendResult<Boolean> {
        // 1️⃣ Verificar si el usuario ya es miembro de la zona
        val alreadyMember = withContext(Dispatchers.IO) {
            isUserInZone(request.zoneId, request.receiverId)
        }

        if (alreadyMember) {
            return SuspendResult.Error("El usuario ya es miembro de la zona")
        }

        // 2️⃣ Verificar si ya existe una solicitud pendiente
        val existingRequests = withContext(Dispatchers.IO) {
            getUserRequests(request.receiverId).getOrNull() ?: emptyList()
        }
        val alreadyRequested = existingRequests.any {
            it.requesterId == request.requesterId && it.zoneId == request.zoneId
        }

        if (alreadyRequested) {
            return SuspendResult.Error("Ya existe una solicitud pendiente para este usuario")
        }

        // 3️⃣ Si todo bien, insertamos la nueva solicitud
        return insertUserRequest(request)
    }

    // 🔹 Obtener solicitudes de un usuario
    override suspend fun getUserRequests(userId: String): SuspendResult<List<ZoneRequestFirestore>> {
        return executeFirestoreOperation {
            getUserRequestsCollection(userId).get().await()
                .toObjects(ZoneRequestFirestore::class.java)
        }
    }

    // 🔹 Borrar solicitud
    override suspend fun deleteUserRequest(
        userId: String,
        requestId: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getUserRequestsCollection(userId)
                .document(requestId)
                .delete()
                .await()
            true
        }
    }

    // 🔹 Verifica si un usuario ya es miembro de la zona
    private suspend fun isUserInZone(zoneId: String?, userId: String?): Boolean {
        if (zoneId == null || userId == null) return false

        return executeFirestoreOperation {
            val zoneDoc = fs.collection(FIRESTORE_ZONES_COLLECTION).document(zoneId).get().await()
            val members = zoneDoc.get(FIRESTORE_ZONE_MEMEBERS_FIELD) as? List<String> ?: emptyList()
            members.contains(userId)
        }.getOrNull() ?: false
    }
}