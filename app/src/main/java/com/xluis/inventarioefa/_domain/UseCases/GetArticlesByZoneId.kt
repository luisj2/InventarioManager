package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetArticlesByZoneId(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val articleZoneRepository: ArticleZoneFirestoreRepository
) {

    operator fun invoke(
        zoneId: String,
        storageType: StorageType
    ): SuspendResult<Flow<List<Article>>> {
        return obtainArticleListInDatabase(zoneId, storageType)
    }

    private fun obtainArticleListInDatabase(
        zoneId: String,
        storageType: StorageType
    ): SuspendResult<Flow<List<Article>>> {
        return when (storageType) {

            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull()
                    ?: return SuspendResult.Error("No se ha encontrado la zona")

                val flow = zoneRoomRepository
                    .getArticleListByZoneIdFlow(zoneIdRoom)
                    .map { list ->
                        list.map { it.toDomain() }
                    }

                SuspendResult.Success(flow)
            }

            StorageType.FIREBASE -> {
                val flow = articleZoneRepository
                    .getArticleListByZoneIdFlow(zoneId)
                    .map { list ->
                        list.map { it.toDomain() }
                    }

                SuspendResult.Success(flow)
            }
        }
    }
}