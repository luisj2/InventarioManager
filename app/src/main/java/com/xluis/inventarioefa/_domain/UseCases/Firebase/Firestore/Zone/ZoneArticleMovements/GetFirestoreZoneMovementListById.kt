package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetFirestoreZoneMovementListById(
    private val movementRepository: ArticleMovementRepository
) {
    suspend operator fun invoke(
        zoneId: String
    ): SuspendResult<List<ArticleMovement>> {
        return when (val result = movementRepository.getMovementsListByZoneId(zoneId)) {
            is SuspendResult.Success -> {
                SuspendResult.Success(result.data.map { it.toDomain() })
            }
            is SuspendResult.Error -> result
            else -> SuspendResult.Error("Estado inválido")
        }
    }
}