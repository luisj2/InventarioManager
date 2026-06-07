package com.xluis.inventarioefa.data.Model.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category : String,
    val descriptions : List<String>
)
