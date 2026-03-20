package com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected

import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Database.Room.MovementSelected.MovementSelectedRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class ClearAllArticleAndMovementSelected(
    private val articleRepo: ArticleSelectedRepository,
    private val movementRepo: MovementSelectedRepository
) {

    suspend operator fun invoke(
        screenId: String,
        zoneId: String
    ): SuspendResult<Boolean> {
        return articleRepo.clearAllByScreenAndZone(screenId, zoneId)
            .flatMap { articlesCleared ->
                if (!articlesCleared) {
                    // Si no se eliminaron artículos, devolvemos Error
                    SuspendResult.Error("No se pudieron limpiar los artículos")
                } else {
                    // Si se eliminaron artículos, limpiamos movimientos
                    movementRepo.clearAllByScreenAndZone(screenId, zoneId)
                }
            }
    }
}