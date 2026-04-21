package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore
import com.xluis.inventarioefa.data.Mapper.Article.toZoneEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class UpdateArticleCount(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {
    suspend operator fun invoke(
        zoneId: String,
        storageType: StorageType,
        articleToUpdate: Article,
    ): SuspendResult<Boolean> {
        return updateZone(zoneId, storageType, articleToUpdate)
    }

    private suspend fun updateZone(
        zoneId: String,
        storageType: StorageType,
        articleToUpdate : Article
    ): SuspendResult<Boolean> {
        return when (storageType) {
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontado la zona")
                zoneRoomRepository.insertOrUpdateArticleCount(zoneIdRoom,articleToUpdate.toZoneEntity())
            }

            StorageType.FIREBASE -> {
                zoneFirestoreRepository.insertOrUpdateArticle(zoneId,articleToUpdate.toFirestore())
            }
        }
    }
}