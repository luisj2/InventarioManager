package com.xluis.inventarioefa._domain.model.DataClass.Zone

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

data class Zone (
    var id: String? = null,
    val name: String = "",
    val storageType: StorageType = StorageType.LOCAL,
    val ownerId : String? = null,
    val membersId : List<String>? = null,
    val childIdList: List<String>? = null,
    val parentIdList : List<String>? = null,
    val articleList: List<Article> = emptyList(),
    val movementList : List<ArticleMovement> = emptyList()
)