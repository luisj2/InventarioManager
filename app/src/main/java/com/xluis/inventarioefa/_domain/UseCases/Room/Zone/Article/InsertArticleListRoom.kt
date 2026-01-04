package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article

import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class InsertArticleListRoom (
    private val articlerepository : ArticleZoneRoomRepository
) {
    suspend operator fun invoke(
        articleList : List<ArticleZoneEntity>
    ) : SuspendResult<Boolean>{
        return articlerepository.insertArticles(articleList)
    }
}