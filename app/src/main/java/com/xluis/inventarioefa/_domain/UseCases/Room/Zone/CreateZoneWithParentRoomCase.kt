package com.xluis.inventarioefa._domain.UseCases.Room.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository

class CreateZoneWithParentRoomCase(
    private val zoneRoomRepository: ZoneRoomRepository
) {

    suspend operator fun invoke(child: Zone, parentId: Long?): ValidationResult {
        return zoneRoomRepository.insertZoneWithHierarchy(child, parentId).toValidationResult()
    }
}
