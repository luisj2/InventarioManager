package com.xluis.inventarioefa.data.Model.Firestore

import com.xluis.inventarioefa.data.Model.Firestore.Article.ArticleFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Zone.ZoneFirestore

data class ZoneFullFirestore(
    val zone: ZoneFirestore,
    val articleList: List<ArticleFirestore>,
    val movementList: List<ArticleMovementFirestore>
)
