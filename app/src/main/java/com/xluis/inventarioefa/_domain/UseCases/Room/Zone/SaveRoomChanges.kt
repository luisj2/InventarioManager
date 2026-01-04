package com.xluis.inventarioefa._domain.UseCases.Room.Zone

import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.getListOrEmpty
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleZoneMovementRoomRepository
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class SaveRoomChanges(
    private val articleRepository: ArticleZoneRoomRepository,
    private val movementRepository: ArticleZoneMovementRoomRepository
) {

    suspend operator fun invoke(
        articlesToSave: List<ArticleZoneEntity>,
        movementsToSave: List<ArticleMovementsEntity>,
        zoneId: Long
    ): SuspendResult<Boolean> {

        val originalArticles = articlesToSave.mapNotNull { article ->
            articleRepository.getArticleFromZone(article.id, zoneId).getOrNull()
        }
        val originalMovements = movementRepository.getMovementsListByZoneId(zoneId).getOrNull() ?: emptyList()

        // Ejecutar operaciones con rollback en caso de error
        return articleRepository.upsertArticleCountList(
            zoneId,
            articlesToSave.map { it.copy(zoneId = zoneId) }
        ).onError { rollback(zoneId, originalArticles, originalMovements) }
            .flatMap {
                movementRepository.insertMovements(
                    movementsToSave.map { it.copy(zoneId = zoneId) }
                ).onError { rollback(zoneId, originalArticles, originalMovements) }
            }
            .onSuccess { true }
    }

    private suspend fun rollback(
        zoneId: Long,
        originalArticles: List<ArticleZoneEntity>,
        originalMovements: List<ArticleMovementsEntity>
    ) {
        // Restaurar artículos originales
        if (originalArticles.isNotEmpty()) {
            articleRepository.insertArticles(originalArticles)
        }

        // Limpiar movimientos actuales y restaurar los originales
        val currentMovements = movementRepository.getMovementsListByZoneId(zoneId).getListOrEmpty()
        if (currentMovements.isNotEmpty()) {
            val currentIds = currentMovements.mapNotNull { it.id }
            if (currentIds.isNotEmpty()) movementRepository.deleteMovementsByIdList(currentIds, zoneId)
        }

        if (originalMovements.isNotEmpty()) {
            movementRepository.insertMovements(originalMovements)
        }
    }
}

