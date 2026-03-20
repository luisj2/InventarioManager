package com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected

import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleSelectedEntity
import kotlinx.coroutines.flow.Flow

class GetArticlesByScreenAndZoneIds(
    private val repo : ArticleSelectedRepository
) {
    operator fun invoke(
        screenId : String,
        zoneId : String
    ) : Flow<List<ArticleSelectedEntity>> {
        return repo.getArticlesByScreenAndZoneIds(screenId, zoneId)
    }

}