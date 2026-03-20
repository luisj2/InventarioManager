package com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.Zone

import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.flow.Flow

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

    fun getMovementListByZoneIdFlow(zoneId: String): Flow<List<ArticleMovementFirestore>>


    //DELETE
    suspend fun deleteMovementsByZoneId (zoneId : String) : SuspendResult<Boolean>

}
