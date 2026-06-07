package com.xluis.inventarioefa.data.Mapper.Article

import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import com.xluis.inventarioefa.data.Model.Room.ArticleSelectedEntity

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
        descriptions = this.descriptions,
        count = 1,
    )
}

fun Article.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = this.id.toLongOrNull() ?: 0,
        name = this.name,
        category = this.category.displayName,
        descriptions = this.descriptions
    )
}

// Room → Domain
fun ArticleSelectedEntity.toDomain(): Article {
    return Article(
        id = this.articleId,
        name = this.name,
        category = try {
            ArticleCategory.valueOf(this.category)
        } catch (e: IllegalArgumentException) {
            ArticleCategory.OTHER
        },
        zoneId = this.zoneId.toString(),
        count = this.count,
        descriptions = this.descriptions
    )
}

// Domain → Room
fun Article.toSelectedEntity(screenId: String): ArticleSelectedEntity {
    return ArticleSelectedEntity(
        screenId = screenId,
        articleId = this.id,
        name = this.name,
        category = this.category.name,
        descriptions = this.descriptions,
        zoneId = this.zoneId ?: "",
        count = this.count
    )
}

