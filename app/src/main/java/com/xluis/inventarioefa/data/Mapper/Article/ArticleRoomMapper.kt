package com.xluis.inventarioefa.data.Mapper.Article

import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity

fun ArticleEntity.toDomain(): Article {
    val categoryEnum = try {
        ArticleCategory.valueOf(this.category.uppercase())
    } catch (e: IllegalArgumentException) {
        ArticleCategory.OTHER
    }

    return Article(
        id = this.id.toString(),
        name = this.name,
        zoneId = null,
        category = categoryEnum,
        count = 1,
    )
}

fun Article.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = this.id.toLongOrNull() ?: 0,
        name = this.name,
        category = this.category.displayName
    )
}

