package com.xluis.inventarioefa._domain.UseCases.Room.Zone

import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetRoomZoneNameById(
    private val zoneRoomRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(
        zoneId : Long
    ) : SuspendResult<String>{
        return zoneRoomRepository.getZoneNameById(zoneId)
    }
}