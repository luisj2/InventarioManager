package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository

class UpdateRoomArticleCount(
    private val articleRepository : ArticleZoneRoomRepository
) {
    suspend operator fun invoke(
        zoneId : Long,
        articleId : Long,
        newCount : Int
    ) : ValidationResult{
        return articleRepository.updateArticleCount(zoneId,articleId,newCount).toValidationResult()
    }
}