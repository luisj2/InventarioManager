package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Mapper.toFirestore

class AddMovementListInZone(
    private val movementRepository: ArticleMovementRepository
) {
    suspend operator fun invoke(
        zoneId : String,
        movementList : List<ArticleMovement>
    ) : ValidationResult{
        return movementRepository.insertMovementsList(zoneId,movementList.map { it.toFirestore() }).toValidationResult("No se han podido añadir los movimientos")
    }
}