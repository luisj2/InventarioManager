package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class DeleteArticleDescriptionList(
    private val roomRepository: ArticleZoneRoomRepository,
    private val firestoreArticleRepository: ArticleZoneFirestoreRepository,
) {

    suspend operator fun invoke(
        zoneId: String,
        articleId: String,
        descriptionsToRemove: List<String>,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return deleteArticleDescriptionList(
            zoneId,
            articleId,
            descriptionsToRemove,
            storageType
        )
    }

    private suspend fun deleteArticleDescriptionList(
        zoneId: String,
        articleId: String,
        descriptionsToRemove: List<String>,
        storageType: StorageType
    ): SuspendResult<Boolean> {

        return when (storageType) {

            StorageType.LOCAL -> {
                val roomZoneId = zoneId.toLongOrNull()
                    ?: return SuspendResult.Error("ZoneId inválido")

                val roomArticleId = articleId.toLongOrNull()
                    ?: return SuspendResult.Error("ArticleId inválido")

                roomRepository.deleteArticleDescriptions(
                    zoneId = roomZoneId,
                    articleId = roomArticleId,
                    descriptionsToRemove = descriptionsToRemove
                )
            }

            StorageType.FIREBASE -> {
                firestoreArticleRepository.deleteArticleDescriptionList(
                    zoneId = zoneId,
                    articleId = articleId,
                    descriptionsToRemove = descriptionsToRemove
                )
            }
        }
    }
}