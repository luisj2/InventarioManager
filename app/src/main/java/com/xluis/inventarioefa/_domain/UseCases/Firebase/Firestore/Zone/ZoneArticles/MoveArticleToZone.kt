package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.successOrFalse
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleZoneMovementRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore
import com.xluis.inventarioefa.data.Mapper.Article.toZoneEntity
import com.xluis.inventarioefa.data.Mapper.toEntity
import com.xluis.inventarioefa.data.Mapper.toFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class MoveArticleToZone(
    private val articleFirestoreRepository: ArticleZoneFirestoreRepository,
    private val movementFirestoreRepository: ArticleMovementRepository,
    private val articleRoomRepository: ArticleZoneRoomRepository,
    private val movementRoomRepository: ArticleZoneMovementRoomRepository
) {

    suspend operator fun invoke(
        zoneIdFrom: String,
        storageFrom: StorageType,
        zoneIdTo: String,
        storageTo: StorageType,
        article: Article,
        countToMove: Int,
        userId: String
    ): ValidationResult {

        if (countToMove <= 0 || article.id.isBlank())
            return ValidationResult.Error("Cantidad o artículo inválido")

        val articleId = article.id

        val movementTake = ArticleMovement(
            articleId = article.id,
            zoneId = zoneIdFrom,
            articleName = article.name,
            userId = userId,
            count = countToMove,
            actionType = MovementAction.TAKE
        )

        val movementAdd = ArticleMovement(
            articleId = article.id,
            zoneId = zoneIdTo,
            articleName = article.name,
            userId = userId,
            count = countToMove,
            actionType = MovementAction.ADD
        )

        val removeResult =
            removeFromSource(zoneIdFrom, storageFrom, articleId, article, countToMove)
        if (removeResult is ValidationResult.Error) return removeResult

        val insertResult =
            insertToDestination(zoneIdTo, storageTo, article, countToMove, zoneIdFrom, storageFrom)
        if (insertResult is ValidationResult.Error) return insertResult

        return recordMovements(
            listOf(movementTake, movementAdd),
            listOf(storageFrom, storageTo),
            listOf(zoneIdFrom, zoneIdTo)
        )
    }


    private suspend fun removeFromSource(zoneId: String, storage: StorageType, articleId: String, article: Article, count: Int): ValidationResult {
        return try {
            val success = when(storage) {
                StorageType.LOCAL -> {
                    val zid = zoneId.toLongOrNull() ?: return ValidationResult.Error("ZoneId inválido")
                    val aid = articleId.toLongOrNull() ?: return ValidationResult.Error("ArticleId inválido")
                    articleRoomRepository.removeArticleCount(zid, aid, count).successOrFalse()
                }
                StorageType.FIREBASE -> articleFirestoreRepository.removeArticleCount(zoneId, articleId, count).successOrFalse()
            }
            if(success) ValidationResult.Success else ValidationResult.Error("No se pudo eliminar del origen")
        } catch(e: Exception) {
            ValidationResult.Error("Error eliminando del origen: ${e.message}")
        }
    }

    private suspend fun insertToDestination(zoneId: String, storage: StorageType, article: Article, count: Int, zoneIdFrom: String, storageFrom: StorageType): ValidationResult {
        return try {
            val success = when(storage) {
                StorageType.LOCAL -> {
                    val zid = zoneId.toLongOrNull() ?: return ValidationResult.Error("ZoneId inválido")
                    articleRoomRepository.addArticleCount(zid, article.toZoneEntity(), count)
                }
                StorageType.FIREBASE -> articleFirestoreRepository.addArticleCount(zoneId, article.toFirestore(), count)
            }
            if(success.successOrFalse()) ValidationResult.Success
            else {
                rollback(zoneIdFrom, storageFrom, article, count)
                ValidationResult.Error("No se pudo insertar en destino, rollback realizado")
            }
        } catch(e: Exception) {
            rollback(zoneIdFrom, storageFrom, article, count)
            ValidationResult.Error("Error insertando en destino: ${e.message}, rollback realizado")
        }
    }

    // Ahora solo recibe los movimientos y los guarda
    private suspend fun recordMovements(
        movements: List<ArticleMovement>,
        storages: List<StorageType>,
        zoneIds: List<String>
    ): ValidationResult {
        return try {
            movements.forEachIndexed { index, movement ->
                val storage = storages[index]
                val zoneId = zoneIds[index]
                when(storage) {
                    StorageType.LOCAL -> movementRoomRepository.insertMovement(movement.toEntity())
                    StorageType.FIREBASE -> movementFirestoreRepository.insertMovement(zoneId, movement.toFirestore())
                }
            }
            ValidationResult.Success
        } catch(e: Exception) {
            ValidationResult.Error("Error registrando movimientos: ${e.message}")
        }
    }

    private suspend fun rollback(zoneIdFrom: String, storageFrom: StorageType, article: Article, count: Int) {
        when(storageFrom) {
            StorageType.LOCAL -> {
                val zid = zoneIdFrom.toLongOrNull() ?: return
                val aid = article.id?.toLongOrNull() ?: return
                articleRoomRepository.addArticleCount(zid, article.toZoneEntity(), count)
            }
            StorageType.FIREBASE -> articleFirestoreRepository.addArticleCount(zoneIdFrom, article.toFirestore(), count)
        }
    }

}

