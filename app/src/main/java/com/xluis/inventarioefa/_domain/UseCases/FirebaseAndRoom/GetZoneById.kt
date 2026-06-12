package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetZoneById(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    operator fun invoke(
        zoneId: String,
        storageType: StorageType
    ): Flow<Zone> =
        obtainZoneByIdInDatabase(zoneId, storageType)

    private fun obtainZoneByIdInDatabase(
        zoneId: String,
        storageType: StorageType
    ): Flow<Zone> {

        return when (storageType) {

            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull()
                    ?: throw IllegalArgumentException("Id inválido")

                zoneRoomRepository
                    .getZoneFullFlow(zoneIdRoom)
                    .map { zoneFull ->
                        requireNotNull(zoneFull) {
                            "No se ha encontrado la zona"
                        }
                        zoneFull.toDomain()
                    }
            }

            StorageType.FIREBASE -> {
                zoneFirestoreRepository
                    .getFullZoneByIdFlow(zoneId)
                    .map { zoneFull ->
                        zoneFull.toDomain()
                    }
            }
        }
    }
}