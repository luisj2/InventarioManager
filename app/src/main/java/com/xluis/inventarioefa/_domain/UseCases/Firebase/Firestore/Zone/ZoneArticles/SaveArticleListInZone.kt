package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Model.Article.ArticleFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class SaveArticleListInZone(
    private val articleRepository: ArticleZoneFirestoreRepository
) {
    suspend operator fun invoke(
        zoneId : String,
        articleList : List<ArticleFirestore>
    ) : SuspendResult<Boolean>{
        return articleRepository.insertArticleList(zoneId,articleList)
    }
}