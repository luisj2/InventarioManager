package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Model.Movement.ArticleMovementFirestore

class SaveMovementInZone(
    private val movementRepository: ArticleMovementRepository
) {
    suspend operator fun invoke(
        zoneId : String,
        movement : ArticleMovementFirestore
    ) : ValidationResult{
        return movementRepository.insertMovement(zoneId,movement).toValidationResult("No se ha podido guardar el movimiento en la zona")
    }
}