package com.xluis.inventarioefa._domain.UseCases.Room.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetRoomZone(
    private val zoneRoomRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(zoneId : String) : SuspendResult<Zone?> {
        return when(val result = zoneRoomRepository.getZoneFull(zoneId.toLongOrNull() ?: 0)){
            is SuspendResult.Success -> {
                val zoneFull = result.data
                val zoneDomain = zoneFull?.toDomain()
                SuspendResult.Success(zoneDomain)
            }
            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Error inesperado")
        }
    }
}