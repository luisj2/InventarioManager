package com.xluis.inventarioefa._domain.UseCases.Room

import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.map
import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Database.Room.MovementSelected.MovementSelectedRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleSelectedEntity
import com.xluis.inventarioefa.data.Model.Room.MovementSelectedEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class AddArtilesAndMovementSelected(
    private val articleRepo: ArticleSelectedRepository,
    private val movementRepo: MovementSelectedRepository
) {

    suspend operator fun invoke(
        articleSelectedList: List<ArticleSelectedEntity>,
        movementSelectedList: List<MovementSelectedEntity>
    ): SuspendResult<Boolean> {

        return articleRepo.insertOrUpdateArticles(articleSelectedList)
            .flatMap { articleOk ->

                if (!articleOk) {
                    SuspendResult.Error("No se pudieron insertar los artículos")
                } else {

                    movementRepo.insertOrUpdateMovements(movementSelectedList)
                        .map { movementOk ->
                            articleOk && movementOk
                        }
                }
            }
    }
}