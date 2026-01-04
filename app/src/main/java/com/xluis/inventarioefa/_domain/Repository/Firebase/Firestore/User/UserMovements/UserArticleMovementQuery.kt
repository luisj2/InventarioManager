package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserMovements

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface UserArticleMovementQuery {
    suspend fun getAllArticleMovementsIdByUserId(userId: String): SuspendResult<List<String>>


}