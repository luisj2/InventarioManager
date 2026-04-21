package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class GetZoneNameById(
    private val zoneFirestoreRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(
        zoneId: String,
        storageType: StorageType
    ): SuspendResult<String> {
        return obtainZoneNameByZoneId(zoneId, storageType)
    }

    private suspend fun obtainZoneNameByZoneId(
        zoneId: String,
        storageType: StorageType
    ): SuspendResult<String> {
        return when (storageType) {
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontado la zona")

                zoneRoomRepository.getZoneNameById(zoneIdRoom)
            }

            StorageType.FIREBASE -> { zoneFirestoreRepository.getZoneNameById(zoneId) }
        }
    }
}