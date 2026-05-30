package com.xluis.inventarioefa._domain.model.Articles

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article

data class ArticleGroupKey(
    val article: Article,
    val articleNum : Int
)
