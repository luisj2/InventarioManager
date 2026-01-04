package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.Article.toFirestore

class AddArticlesInZone(
    private val articleRepository: ArticleZoneFirestoreRepository
) {

    suspend operator fun invoke(
        zoneId : String,
        articleListToAdd : List<Article>
    ) : ValidationResult {
        return articleRepository.insertArticleList(zoneId,articleListToAdd.map { it.toFirestore() }).toValidationResult("No se pudieron añadir los artículos")
    }
}