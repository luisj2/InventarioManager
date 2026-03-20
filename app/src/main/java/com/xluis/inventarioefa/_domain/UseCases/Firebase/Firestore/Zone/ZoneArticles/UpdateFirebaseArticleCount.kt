package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository

class UpdateFirebaseArticleCount(
    private val articleRepository: ArticleZoneFirestoreRepository
) {

    suspend operator fun invoke(
        zoneId: String,
        articleId: String,
        newCount: Int
    ) : ValidationResult{
        return articleRepository.changeArticleCount(zoneId,articleId,newCount).toValidationResult()
    }
}