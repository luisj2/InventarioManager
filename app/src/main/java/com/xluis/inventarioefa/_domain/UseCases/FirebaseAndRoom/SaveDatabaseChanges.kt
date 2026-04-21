package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore
import com.xluis.inventarioefa.data.Mapper.Article.toZoneEntity
import com.xluis.inventarioefa.data.Mapper.toEntity
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class SaveDatabaseChanges(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(
        zoneId : String,
        articlesToSave : List<Article>,
        movementsToSave : List<ArticleMovement>,
        storageType: StorageType
    ) : SuspendResult<Boolean>{
        return saveChangesInDatabase(zoneId,articlesToSave,movementsToSave,storageType)
    }

    private suspend fun saveChangesInDatabase (
        zoneId : String,
        articlesToSave : List<Article>,
        movementsToSave : List<ArticleMovement>,
        storageType: StorageType
    ) : SuspendResult<Boolean>{
        val articles = articlesToSave.map { it.copy(zoneId = zoneId) }
        val movements = movementsToSave.map { it.copy(zoneId = zoneId) }
        return when(storageType){
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encontado el identificador de la zona")
                val roomArticles = articles.map { it.toZoneEntity() }
                val roomMovements = movements.map { it.toEntity() }
                zoneRoomRepository.upsertArticlesAndMovements(zoneIdRoom,roomArticles,roomMovements)
            }
            StorageType.FIREBASE -> {
                val firestoreArticles = articles.map { it.toFirestore() }
                val firestoreMovements = movements.map { it.toFirestore() }
                zoneFirestoreRepository.upsertArticleAndMovementLists(zoneId,firestoreArticles,firestoreMovements)
            }
        }
    }
}