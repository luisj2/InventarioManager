package com.xluis.inventarioefa._domain.UseCases.Room.Article

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Room.Articles.ArticleRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetAllArticles(
    private val articleRepository: ArticleRepository
) {
    suspend operator fun invoke() : SuspendResult<List<Article>> {
        return when (val result = articleRepository.getAllArticles()) {
            is SuspendResult.Success -> SuspendResult.Success(result.data.map { it.toDomain() })
            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Error inesperado al obtener los artículos")
        }
    }
}
