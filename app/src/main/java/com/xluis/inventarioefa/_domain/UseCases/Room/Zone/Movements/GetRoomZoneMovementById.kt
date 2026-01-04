package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetRoomZoneMovementById(
    private val zoneMovementRepository: ZoneRoomRepository
) {

    suspend operator fun invoke(
        zoneId: Long
    ): SuspendResult<List<ArticleMovement>> {

        return when (val result = zoneMovementRepository.getMovementListByZoneId(zoneId)) {

            is SuspendResult.Success -> {
                val movementList = result.data.map { it.toDomain() }
                SuspendResult.Success(movementList)
            }

            is SuspendResult.Error ->
                SuspendResult.Error(result.message)

            else ->
                SuspendResult.Error("Error inesperado")
        }
    }

}