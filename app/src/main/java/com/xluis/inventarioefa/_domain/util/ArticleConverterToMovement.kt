package com.xluis.inventarioefa._domain.util

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import java.time.LocalDateTime




fun articleToArticleMovement(
    article: Article,
    userId: String,
    action: MovementAction,
    userName : String,
    zoneId: String? = null,
    zoneName: String? = null,
    date: LocalDateTime = LocalDateTime.now()
): ArticleMovement {

    return ArticleMovement(
        articleId = article.id ?: "",
        userId = userId,
        actionType = action,
        userName = userName,
        count = article.count,
        articleName = article.name,
        zoneId = zoneId ?: "",
        zoneName = zoneName ?: "",
        date = date
    )
}

