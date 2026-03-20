package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User

import com.xluis.inventarioefa.data.Model.Firestore.User.UserFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface UserFirestoreQuery {
    // INSERT
    suspend fun insertUser(userFirestore: UserFirestore): SuspendResult<Boolean>

    // GET
    suspend fun getUserById(userId: String): SuspendResult<UserFirestore?>

    suspend fun getUserNameById (userId : String) : SuspendResult<String>

    suspend fun getIdByEmail (email : String) : SuspendResult<String>
}
