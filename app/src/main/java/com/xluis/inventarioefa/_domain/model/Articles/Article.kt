package com.xluis.inventarioefa._domain.model.DataClass.Articles

import ArticleCategory
import java.util.UUID

data class Article(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description : String? = null,
    val category: ArticleCategory = ArticleCategory.OTHER,
    val zoneId: String? = null,
    val count: Int = 1
)