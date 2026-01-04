package com.xluis.inventarioefa.data.Database.Firestore.User

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Model.User.UserFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserFirestoreQuery
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_EMAIL_FIELD
import kotlinx.coroutines.tasks.await
class UserFirestoreRepository(
    private val fs: FirebaseFirestore
) : UserFirestoreQuery, BaseFirestoreRepository(fs) {


    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)


    override suspend fun insertUser(userFirestore: UserFirestore): SuspendResult<Boolean> =
        executeFirestoreOperation {
            saveUser(userFirestore)
            true
        }


    private suspend fun saveUser(userFirestore: UserFirestore) {
        getUserCollection().document(userFirestore.uid).set(userFirestore).await()
    }

    override suspend fun getUserById(userId: String): SuspendResult<UserFirestore?> =
        executeFirestoreOperation {
            getUserDocumentById(userId)
        }

    override suspend fun getIdByEmail(email: String): SuspendResult<String> {
        return executeFirestoreOperation {
            val querySnapshot = getUserCollection()
                .whereEqualTo(FIRESTORE_USER_EMAIL_FIELD, email)
                .get()
                .await()

            val document = querySnapshot.documents.firstOrNull()
                ?: throw Exception("No existe un usuario con ese email.")

            document.id
        }
    }




    private suspend fun getUserDocumentById(userId: String): UserFirestore? =
        getUserCollection().document(userId).get().await().toObject(UserFirestore::class.java)
}




