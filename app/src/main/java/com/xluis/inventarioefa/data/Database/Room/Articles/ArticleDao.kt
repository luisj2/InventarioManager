package com.xluis.inventarioefa.data.Database.Room.Articles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity

@Dao
interface ArticleDao {

    //INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle (newArticle : ArticleEntity) : Long

     @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles (articleList : List<ArticleEntity>)

    //GET
    @Query("SELECT * FROM articles")
    suspend fun getAllArticles () : List<ArticleEntity>
}