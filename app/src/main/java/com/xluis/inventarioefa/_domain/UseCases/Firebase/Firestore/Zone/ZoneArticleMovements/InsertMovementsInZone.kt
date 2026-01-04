package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Model.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class InsertMovementsInZone(
    private val movementRepository: ArticleMovementRepository

) {
    suspend operator fun invoke(
        zoneId : String,
        movementList : List<ArticleMovementFirestore>
    ) :SuspendResult<Boolean>{
        return movementRepository.insertMovementsList(zoneId,movementList)
    }
}