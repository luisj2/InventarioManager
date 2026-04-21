package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class RemoveArticlesByIdList(
    private val articleRoomrepository: ArticleZoneRoomRepository,
    private val articleFirestoreRepository: ArticleZoneFirestoreRepository,
) {
    suspend operator fun invoke(
        zoneId: String,
        articleIdList: List<String>,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return removeDatabaseArticles(zoneId, articleIdList, storageType)
    }

    private suspend fun removeDatabaseArticles(
        zoneId: String,
        articleIdList: List<String>,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return when(storageType){
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontrado la zona")
                val articleRoomIdList = articleIdList.mapNotNull { it.toLongOrNull()  }
                articleRoomrepository.removeArticleByIdList(zoneIdRoom,articleRoomIdList)
            }
            StorageType.FIREBASE -> {
                articleFirestoreRepository.removeArticleListByIdList(zoneId,articleIdList)
            }
        }
    }
}