package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.util.map
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class GetZoneArticleById(
    private val articleZoneFirestoreRepository: ArticleZoneFirestoreRepository,
    private val articleZoneRoomRepository: ArticleZoneRoomRepository
) {
    suspend operator fun invoke(
        zoneId: String,
        articleId: String,
        storageType : StorageType
    ): SuspendResult<Article?> {
        return when(storageType){
           StorageType.LOCAL -> {
               val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontrado la zona")
               val artileIdRoom = articleId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontrado el artículo")

               articleZoneRoomRepository.getArticleFromZone(artileIdRoom,zoneIdRoom).map { it?.toDomain() }
           }
           StorageType.FIREBASE -> {
               articleZoneFirestoreRepository.getArticleById(zoneId,articleId).map { it?.toDomain() }
           }
       }
    }

}