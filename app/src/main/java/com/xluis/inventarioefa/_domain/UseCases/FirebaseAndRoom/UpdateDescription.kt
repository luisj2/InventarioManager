package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class UpdateDescription(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(
        zoneId: String,
        storageType: StorageType,
        articleId: String,
        oldDescription: String,
        newDescription: String
    ): SuspendResult<Boolean> {
        return updateZone(
            zoneId,
            storageType,
            articleId,
            oldDescription,
            newDescription
        )
    }

    private suspend fun updateZone(
        zoneId: String,
        storageType: StorageType,
        articleId: String,
        oldDescription: String,
        newDescription: String
    ): SuspendResult<Boolean> {

        return when (storageType) {

            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull()
                    ?: return SuspendResult.Error("No se ha encontrado la zona")

                zoneRoomRepository.updateArticleDescription(
                    zoneId = zoneIdRoom,
                    articleId = articleId.toLong(),
                    oldDescription = oldDescription,
                    newDescription = newDescription
                )
            }

            StorageType.FIREBASE -> {
                zoneFirestoreRepository.updateArticleDescription(
                    zoneId = zoneId,
                    articleId = articleId,
                    oldDescription = oldDescription,
                    newDescription = newDescription
                )
            }
        }
    }
}