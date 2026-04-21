package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class ChangeZoneName(
    private val zoneFirestoreRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {

    suspend operator fun invoke(
        zoneId: String,
        newZoneName: String,
        storageType : StorageType
    ): SuspendResult<Boolean> {
        return changeZoneName(zoneId,newZoneName,storageType)
    }

    private suspend fun changeZoneName(zoneId: String, newZoneName: String, storageType: StorageType): SuspendResult<Boolean> {
        return when(storageType){
            StorageType.LOCAL -> {
                val roomId = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontrado la Zona")
                zoneRoomRepository.changeZoneName(roomId,newZoneName)
            }
            StorageType.FIREBASE -> { zoneFirestoreRepository.changeZoneName(zoneId,newZoneName) }
        }
    }
}