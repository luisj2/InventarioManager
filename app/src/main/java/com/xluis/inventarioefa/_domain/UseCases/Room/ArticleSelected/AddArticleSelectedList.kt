package com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected

import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleSelectedEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class AddArticleSelectedList(
    private val repo : ArticleSelectedRepository
) {
    suspend operator fun invoke(
        articleSelectedList : List<ArticleSelectedEntity>
    ) : SuspendResult<Boolean>{
        return repo.insertOrUpdateArticles(articleSelectedList)
    }
}