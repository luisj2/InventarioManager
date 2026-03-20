package com.xluis.inventarioefa.data.Model.Room

import ArticleCategory
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ArticleSelected")
data class ArticleSelectedEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val screenId: String,
    val articleId: String,
    val name: String = "",
    val category: String = ArticleCategory.OTHER.displayName,
    val zoneId: String,
    val count: Int = 1
)