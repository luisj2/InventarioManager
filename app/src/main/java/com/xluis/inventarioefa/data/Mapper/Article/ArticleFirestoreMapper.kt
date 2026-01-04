package com.xluis.inventarioefa.data.Mapper.Article

import ArticleCategory
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Model.Article.ArticleFirestore
import java.util.UUID

// ============================================================
// 🔹 De dominio (Article) a Firestore (ArticleFirestore)
// ============================================================
fun Article.toFirestore(): ArticleFirestore {
    return ArticleFirestore(
        id = this.id,
        name = this.name,
        category = this.category.name,
        zoneId = this.zoneId,
        count = this.count,
    )
}

// ============================================================
// 🔹 De Firestore (ArticleFirestore) a dominio (Article)
// ============================================================
fun ArticleFirestore.toDomain(): Article {
    return Article(
        id = this.id ?: UUID.randomUUID().toString(),
        name = this.name,
        category = ArticleCategory.valueOf(this.category),
        zoneId = this.zoneId,
        count = this.count,
    )
}
