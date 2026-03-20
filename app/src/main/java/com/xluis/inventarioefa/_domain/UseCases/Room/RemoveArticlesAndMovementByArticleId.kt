package com.xluis.inventarioefa._domain.UseCases.Room

import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Database.Room.MovementSelected.MovementSelectedRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class RemoveArticlesAndMovementByArticleId(
    private val articleRepo: ArticleSelectedRepository,
    private val movementRepo: MovementSelectedRepository
) {

    suspend operator fun invoke(
        articleIdList: List<String>,
        screenId: String,
        zoneId: String,
    ): SuspendResult<Boolean> {

        // Primero eliminamos los artículos
        return articleRepo.deleteArticlesByScreenAndZone(articleIdList, screenId, zoneId)
            .flatMap { articlesDeleted ->
                if (!articlesDeleted) {
                    SuspendResult.Error("No se pudieron eliminar los artículos")
                } else {
                    // Luego eliminamos los movimientos relacionados
                    movementRepo.deleteMovementsByScreenAndZone(articleIdList, screenId, zoneId)
                }
            }
    }
}