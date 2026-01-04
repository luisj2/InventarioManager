package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class DeleteRoomArticleList(
    private val articlerepository : ArticleZoneRoomRepository

) {
    suspend operator fun invoke(
        zoneId : Long,
        idList : List<Long>
    ) : SuspendResult<Boolean>{
        return articlerepository.deleteArticleByIdList(zoneId,idList)
    }
}