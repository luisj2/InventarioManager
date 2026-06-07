package com.xluis.inventarioefa.data.Mapper.Article


import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity

fun Article.toZoneEntity(): ArticleZoneEntity {
    return ArticleZoneEntity(
        id = this.id.toLongOrNull() ?: 0,
        name = this.name,
        category = this.category.name,
        descriptions = this.descriptions,
        zoneId = this.zoneId?.toLongOrNull() ?: 0,
        count = this.count
    )
}


fun ArticleZoneEntity.toDomain(): Article {
    return Article(
        id = this.id.toString(),
        name = this.name,
        category = ArticleCategory.valueOf(this.category),
        descriptions = this.descriptions,
        count = this.count,
    )
}
