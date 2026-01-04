package com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.Zone

import com.xluis.inventarioefa.data.Model.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface ArticleMovementQuery {

    // INSERT
    suspend fun insertMovement(
        zoneId: String,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean>

    suspend fun insertMovementsList(
        zoneId: String,
        movementsList: List<ArticleMovementFirestore>
    ): SuspendResult<Boolean>


    //GET

    suspend fun getMovementsListByZoneId(
        zoneId: String
    ): SuspendResult<List<ArticleMovementFirestore>>

    //DELETE
    suspend fun deleteMovementsByZoneId (zoneId : String) : SuspendResult<Boolean>

}
