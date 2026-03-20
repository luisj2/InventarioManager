package com.xluis.inventarioefa._domain.UseCases.Room.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetRoomZoneList(
    private val zoneRoomRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(
        userId : String
    ) : SuspendResult<List<Zone>> {
        return when(val result = zoneRoomRepository.getAllZonesFull(userId)){
            is SuspendResult.Success -> {
                val zoneList = result.data.mapNotNull { zoneFull ->
                    zoneFull?.toDomain()
                }
                SuspendResult.Success(zoneList)
            }
            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Error inesperado")
        }
    }
}