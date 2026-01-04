package com.xluis.inventarioefa.data.Database.Firebase.Firestore.User

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.User.UserZonesRequestsQuery
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Model.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_REQUESTS_FIELD
import kotlinx.coroutines.tasks.await

class UserZonesRequestsRepository(
    private val fs: FirebaseFirestore
) : BaseFirestoreRepository(fs), UserZonesRequestsQuery {
    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)

    private fun getUserRequestsCollection(userId: String) =
        getUserCollection()
            .document(userId)
            .collection(FIRESTORE_USER_REQUESTS_FIELD)

    override suspend fun insertUserRequest(
        userId: String,
        newRequest: ZoneRequestFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getUserRequestsCollection(userId)
                .document(newRequest.id)
                .set(newRequest)
                .await()
            true
        }
    }


    override suspend fun getUserRequests(userId: String): SuspendResult<List<ZoneRequestFirestore>> {
        return executeFirestoreOperation {
            getUserRequestsCollection(userId).get().await()
                .toObjects(ZoneRequestFirestore::class.java)
        }
    }

    override suspend fun deleteUserRequest(
        userId: String,
        requestId   : String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getUserRequestsCollection(userId)
                .document(requestId)
                .delete()
                .await()
            true
        }
    }


}