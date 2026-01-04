package com.xluis.inventarioefa.data.Mapper.Article


import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity

fun Article.toZoneEntity(): ArticleZoneEntity {
    return ArticleZoneEntity(
        id = 0,
        name = this.name,
        category = this.category.name,
        zoneId = this.zoneId?.toLongOrNull() ?: 0
    )
}


fun ArticleZoneEntity.toDomain(): Article {
    return Article(
        id = this.id.toString(),
        name = this.name,
        category = ArticleCategory.valueOf(this.category),
        count = 1,
    )
}
