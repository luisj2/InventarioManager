package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleZoneMovementRoomRepository
import com.xluis.inventarioefa.data.Mapper.toEntity
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class InsertMovements(
    private val movementRoomRepository: ArticleZoneMovementRoomRepository,
    private val movementFirestoreRespository: ArticleMovementRepository

) {
    suspend operator fun invoke(
        zoneId: String,
        movementList: List<ArticleMovement>,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return insertMovementsInDatabase(zoneId, movementList, storageType)
    }

    private suspend fun insertMovementsInDatabase(
        zoneId: String,
        movementList: List<ArticleMovement>,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return when (storageType) {
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull()
                    ?: return SuspendResult.Error("No se ha encontrado la zona")
                movementRoomRepository.insertMovements(movementList.map {
                    it.toEntity().copy(zoneId = zoneIdRoom)
                })
            }

            StorageType.FIREBASE -> {
                movementFirestoreRespository.insertMovementsList(
                    zoneId,
                    movementList.map { it.toFirestore() })
            }
        }
    }
}