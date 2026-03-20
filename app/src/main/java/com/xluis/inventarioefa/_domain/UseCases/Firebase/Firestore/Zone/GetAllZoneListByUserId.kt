package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.map
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class GetAllZoneListByUserId(
    private val zoneFirestoreRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {

    suspend operator fun invoke(
        userId: String?,
        zoneId : String,
        storageType: StorageType
    ): SuspendResult<List<Zone>> {

        val combinedList = mutableListOf<Zone>()
        val fathersIds = getFathersIds(zoneId,storageType)
        // Si userId no es null, obtenemos datos de Firestore
        if (userId != null) {
            val firestoreResult = zoneFirestoreRepository.getUserZones(userId)
                .map { it.map { zone -> zone.toDomain() } }

            if (firestoreResult is SuspendResult.Success) {
                combinedList.addAll(firestoreResult.data)
            }
            val roomResult = zoneRoomRepository.getAllZonesFull(userId)
                .map { it.mapNotNull { zone -> zone?.toDomain() } }
            // Siempre obtenemos datos de Room

            if (roomResult is SuspendResult.Success) {
                combinedList.addAll(roomResult.data)
            } else if (combinedList.isEmpty() && roomResult is SuspendResult.Error) {
                // Si Room falla y no hay datos de Firestore, devolvemos el error
                return roomResult
            }
        }

        val filteredList = combinedList.filter { zone ->
            zone.id !in fathersIds
        }


        return SuspendResult.Success(filteredList)
    }

    private suspend fun getFathersIds(
        zoneId: String,
        storageType: StorageType
    ): List<String> {

        return when (storageType) {

            StorageType.LOCAL -> {
                val roomZoneId = zoneId.toLongOrNull()

                if (roomZoneId != null) {
                    when (val result = zoneRoomRepository.getZoneById(roomZoneId)) {
                        is SuspendResult.Success ->{
                            result.data?.parentIdList?.map { it.toString() } ?: emptyList()
                        }
                        else -> emptyList()
                    }
                } else {
                    emptyList()
                }
            }

            StorageType.FIREBASE -> {
                when (val result = zoneFirestoreRepository.getZoneById(zoneId)) {
                    is SuspendResult.Success -> result.data.parentIdList ?: emptyList()
                    else -> emptyList()
                }
            }
        }
    }
}

