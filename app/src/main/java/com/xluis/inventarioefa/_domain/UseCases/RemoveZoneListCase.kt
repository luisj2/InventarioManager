package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class RemoveZoneListCase(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(
        userId: String,
        zoneList: List<Zone>
    ): ValidationResult {

        for (zone in zoneList) {

            val zoneId = zone.id ?: continue

            removeZone(userId, zoneId, zone.storageType)
        }

        return ValidationResult.Success
    }


    private suspend fun removeZone(
        userId : String,
        zoneId: String,
        storageType: StorageType
    ): ValidationResult =
        when (storageType) {
            StorageType.LOCAL -> zoneId.toLongOrNull()
                ?.let { zoneRoomRepository.removeZone(it) }
                ?.toValidationResult("No se ha podido eliminar la zona")
                ?: ValidationResult.Error("ID inválido para zona local")

            StorageType.FIREBASE -> zoneFirestoreRepository
                .deleteZoneById(userId,zoneId)
                .toValidationResult("No se ha podido eliminar la zona")
        }

}
