package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetZoneArticleById(
    private val articleZoneRepository: ArticleZoneFirestoreRepository,
) {
    suspend operator fun invoke(
        zoneId: String,
        articleId: String
    ): SuspendResult<Article?> {
        return when (val result = articleZoneRepository.getArticleById(zoneId, articleId)) {
            is SuspendResult.Success -> SuspendResult.Success(result.data?.toDomain())
            is SuspendResult.Error -> result
            else -> SuspendResult.Error("Estado inválido")
        }
    }

}