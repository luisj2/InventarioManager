package com.xluis.inventarioefa._domain.UseCases.Room

import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.map
import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Database.Room.MovementSelected.MovementSelectedRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class ClearAllArticleAndMovementSelected (
    private val articleRepo: ArticleSelectedRepository,
    private val movementRepo: MovementSelectedRepository
) {

    suspend operator fun invoke(): SuspendResult<Boolean> {

        return articleRepo.clearAll()
            .flatMap { articleCleared ->

                movementRepo.clearAll()
                    .map { movementCleared ->
                        articleCleared && movementCleared
                    }
            }
    }
}