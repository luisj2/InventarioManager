package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles

import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class RemoveFirestoreArticleList(
    private val articleZoneRepository: ArticleZoneFirestoreRepository,
) {
    suspend operator fun invoke(
        zoneId : String,
        articleIdList : List<String>
    ) : SuspendResult<Boolean>{
        return articleZoneRepository.deleteArticleListByIdList(zoneId,articleIdList)
    }
}