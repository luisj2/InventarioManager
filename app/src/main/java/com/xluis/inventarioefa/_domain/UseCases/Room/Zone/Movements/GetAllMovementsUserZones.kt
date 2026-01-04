package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetAllMovementsUserZones(
    private val zoneRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(): SuspendResult<List<ArticleMovement>> {
        return when (val result = zoneRepository.getAllMovementsByUserZones()) {
            is SuspendResult.Success -> {
                SuspendResult.Success(result.data.map { it.toDomain() })
            }
            is SuspendResult.Error -> {
                SuspendResult.Error(result.message)
            }
            else -> {
                SuspendResult.Error("Error inesperado")
            }
        }
    }

}
