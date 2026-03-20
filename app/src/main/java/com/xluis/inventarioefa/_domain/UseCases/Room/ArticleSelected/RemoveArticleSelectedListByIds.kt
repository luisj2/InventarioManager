package com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected

import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class RemoveArticleSelectedListByIds(
    private val repo : ArticleSelectedRepository
){
    suspend operator fun invoke(
        idList : List<String>,
        zoneId : String,
        screenId : String
    ) : SuspendResult<Boolean>{
        return repo.deleteArticlesByScreenAndZone(idList,screenId, zoneId)
    }
}