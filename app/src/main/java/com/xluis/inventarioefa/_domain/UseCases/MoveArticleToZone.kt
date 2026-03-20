package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore
import com.xluis.inventarioefa.data.Mapper.Article.toZoneEntity
import com.xluis.inventarioefa.data.Mapper.toEntity
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

class MoveArticleToZone(
    private val zoneFirestoreRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {


    suspend operator fun invoke(
        zoneIdFrom: String,
        storageFrom: StorageType,
        zoneIdTo: String,
        storageTo: StorageType,
        article: Article,
        userName : String,
        userId: String
    ): ValidationResult {

        val countToMove = article.count
        if (countToMove <= 0 || article.id.isBlank())
            return ValidationResult.Error("Cantidad o artículo inválido")

        // Crear movimientos TAKE y ADD
        val movementTake = ArticleMovement(
            articleId = article.id,
            zoneId = zoneIdFrom,
            articleName = article.name,
            userId = userId,
            userName = userName,
            count = countToMove,
            actionType = MovementAction.TAKE
        )

        val movementAdd = ArticleMovement(
            articleId = article.id,
            zoneId = zoneIdTo,
            articleName = article.name,
            userId = userId,
            userName = userName,
            count = countToMove,
            actionType = MovementAction.ADD
        )


        // Restar del origen
        updateOrDeleteArticles(
            zoneId = zoneIdFrom,
            articleId = article.id,
            quantityToRemove = countToMove,
            movement = movementTake,
            storageType = storageFrom
        ).onError {
            return ValidationResult.Error(it.message)
        }

        // Sumar al destino
        updateOrInsertArticlesAndMovements(
            zoneId = zoneIdTo,
            article = article,
            quantityToAdd = countToMove,
            movement = movementAdd,
            storageType = storageTo
        ).onError {
            return ValidationResult.Error(it.message)
        }

        return ValidationResult.Success
    }





    private suspend fun updateOrDeleteArticles(
        zoneId : String,
        articleId: String,
        quantityToRemove: Int,
        movement: ArticleMovement,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return when (storageType) {
            StorageType.LOCAL -> {
                val articleRoomId = articleId.toLongOrNull()
                    ?: return SuspendResult.Error("No se ha encontrado el artículo")
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se ha encotrado el identificador de la zona")

                zoneRoomRepository.updateOrDeleteArticlesAndMovements(
                    zoneIdRoom,
                    articleRoomId,
                    quantityToRemove,
                    movement.toEntity()
                )
            }
            StorageType.FIREBASE -> {
                zoneFirestoreRepository.updateOrDeleteArticlesAndMovements(
                    zoneId = zoneId,
                    articleIdToRemove = articleId,
                    quantityToRemove = quantityToRemove,
                    movement = movement.toFirestore()
                )
            }
        }
    }


    private suspend fun updateOrInsertArticlesAndMovements(
        article: Article,
        zoneId : String,
        quantityToAdd: Int,
        movement: ArticleMovement,
        storageType: StorageType
    ): SuspendResult<Boolean> {
        return when (storageType) {
            StorageType.LOCAL -> {
                val zoneIdRoom = zoneId.toLongOrNull() ?: return SuspendResult.Error("No se encontro la zona")
                val articleRoom = article.toZoneEntity().copy(
                    count = quantityToAdd,
                    zoneId = zoneIdRoom
                )
                val entityMovement = movement.toEntity().copy(zoneId = zoneIdRoom)

                zoneRoomRepository.upsertArticleAndMovement(zoneIdRoom,articleRoom, entityMovement)
            }
            StorageType.FIREBASE -> {
                zoneFirestoreRepository.upsertArticleAndMovement(
                    zoneId,
                    quantityToAdd,
                    article.toFirestore(),
                    movement.toFirestore()
                )
            }
        }
    }

}
