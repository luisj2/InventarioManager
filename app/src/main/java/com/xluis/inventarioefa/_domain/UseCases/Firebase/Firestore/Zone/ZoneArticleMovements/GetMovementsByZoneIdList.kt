package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetMovementsByZoneIdList(
    private val movementRepository: ArticleMovementRepository
) {

    suspend operator fun invoke(
        zoneIdList: List<String>
    ): SuspendResult<List<ArticleMovement>> {
        val allMovements = mutableListOf<ArticleMovement>()

        for(zoneId in zoneIdList) {
            when(val result = movementRepository.getMovementsListByZoneId(zoneId)) {
                is SuspendResult.Success -> {
                    allMovements.addAll(result.data.map { it.toDomain() })
                }
                is SuspendResult.Error -> {
                    return SuspendResult.Error("Error al obtener movimientos de la zona $zoneId: ${result.message}")
                }
                else -> {
                    return SuspendResult.Error("Estado inválido al obtener movimientos de la zona $zoneId")
                }
            }
        }

        return SuspendResult.Success(allMovements)
    }



}