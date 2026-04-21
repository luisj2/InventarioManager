package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class GetMovementsByZoneId(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val movementsFirestoreRepository: ArticleMovementRepository
) {

    operator fun invoke(
        zoneId: String,
        storageType: StorageType
    ): Flow<List<ArticleMovement>> {
        return obtainMovementByZoneId(zoneId, storageType)
    }

    private fun obtainMovementByZoneId(
        zoneId: String,
        storageType: StorageType
    ): Flow<List<ArticleMovement>> {
        return when (storageType) {
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull()
                    ?: throw IllegalArgumentException("No se ha encontrado la zona")

                zoneRoomRepository.getMovementsListByZoneIdFlow(zoneIdRoom)
                    .map { list -> list.map { it.toDomain() } }
                    .catch { e -> emit(emptyList()) } // en caso de error, emitimos lista vacía
            }

            StorageType.FIREBASE -> {
                movementsFirestoreRepository.getMovementListByZoneIdFlow(zoneId)
                    .map { list -> list.map { it.toDomain() } }
                    .catch { e -> emit(emptyList()) }
            }
        }
    }
}