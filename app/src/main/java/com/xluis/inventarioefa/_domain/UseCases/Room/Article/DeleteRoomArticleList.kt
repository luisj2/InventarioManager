package com.xluis.inventarioefa._domain.UseCases.Room.Article

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class DeleteRoomArticleList(
    private val repository: ArticleZoneRoomRepository
) {


    suspend operator fun invoke(
        zoneId: Long,
        articleIdList: List<Long>
    ): SuspendResult<Boolean> {
        if (articleIdList.isEmpty()) return SuspendResult.Success(true)
        return repository.removeArticleByIdList(zoneId, articleIdList)
    }
}
