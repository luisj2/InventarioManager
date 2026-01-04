package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.getListOrEmpty
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Model.Article.ArticleFirestore
import com.xluis.inventarioefa.data.Model.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class SaveFirestoreZoneChanges(
    private val articleRepository: ArticleZoneFirestoreRepository,
    private val movementRepository: ArticleMovementRepository,
) {

    suspend operator fun invoke(
        zoneId: String,
        articlesToSave: List<ArticleFirestore>,
        movementsToSave: List<ArticleMovementFirestore>
    ): SuspendResult<Boolean> {

        // 1️⃣ Guardar estado actual para rollback
        val originalArticles = articleRepository.getArticleListByZoneId(zoneId).getListOrEmpty()
        val originalMovements = movementRepository.getMovementsListByZoneId(zoneId).getListOrEmpty()

        // 2️⃣ Ejecutar operaciones con rollback en caso de error
        return articleRepository.upsertArticleList(zoneId, articlesToSave)
            .onError { rollback(zoneId, originalArticles, originalMovements) }
            .flatMap {
                movementRepository.insertMovementsList(zoneId, movementsToSave)
                    .onError { rollback(zoneId, originalArticles, originalMovements) }
            }
            .onSuccess { true }
    }

    private suspend fun rollback(
        zoneId: String,
        originalArticles: List<ArticleFirestore>,
        originalMovements: List<ArticleMovementFirestore>
    ) {
        // Restaurar artículos originales
        if (originalArticles.isNotEmpty()) {
            articleRepository.insertArticleList(zoneId, originalArticles)
        }

        // Limpiar movimientos actuales y restaurar los originales
        val currentMovements = movementRepository.getMovementsListByZoneId(zoneId).getListOrEmpty()
        if (currentMovements.isNotEmpty()) {
            movementRepository.deleteMovementsByZoneId(zoneId)
        }

        if (originalMovements.isNotEmpty()) {
            movementRepository.insertMovementsList(zoneId, originalMovements)
        }
    }
}

