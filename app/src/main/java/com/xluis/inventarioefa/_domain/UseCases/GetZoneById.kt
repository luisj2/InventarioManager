package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class GetZoneById (
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
){

    suspend operator fun invoke (
        zoneId : String,
        storageType: StorageType
    ) : SuspendResult<Zone>{
        return obtainZoneByIdInDatabase(zoneId,storageType)
    }

    private suspend fun obtainZoneByIdInDatabase(
        zoneId : String,
        storageType: StorageType
    ) : SuspendResult<Zone>{
        val notFoundMessage = "No se ha encontrado la zona"
        return  when(storageType){
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error(notFoundMessage)
                val zoneFull = zoneRoomRepository.getZoneFull(zoneIdRoom).getOrNull() ?: return SuspendResult.Error(notFoundMessage)

                SuspendResult.Success(zoneFull.toDomain())
            }
            StorageType.FIREBASE -> {
                val zoneFull = zoneFirestoreRepository.getFullZoneById(zoneId).getOrNull() ?: return SuspendResult.Error(notFoundMessage)
                SuspendResult.Success(zoneFull.toDomain())
            }
        }
    }
}