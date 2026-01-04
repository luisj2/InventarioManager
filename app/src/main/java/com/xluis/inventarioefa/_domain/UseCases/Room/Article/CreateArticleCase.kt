package com.xluis.inventarioefa._domain.UseCases.Room.Article

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.toValidationResult
import com.xluis.inventarioefa.data.Database.Room.Articles.ArticleRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity

class CreateArticleCase(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke(article : ArticleEntity) : ValidationResult{
        return articleRepository.insertArticle(article).toValidationResult()
    }
}