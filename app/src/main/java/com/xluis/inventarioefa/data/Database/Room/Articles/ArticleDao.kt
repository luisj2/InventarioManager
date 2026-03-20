package com.xluis.inventarioefa.data.Database.Room.Articles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    //INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(newArticle: ArticleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articleList: List<ArticleEntity>)

    //GET
    @Query("SELECT * FROM articles")
    suspend fun getAllArticles(): List<ArticleEntity>

    @Query("SELECT * FROM articles")
    fun getAllArticlesFlow(): Flow<List<ArticleEntity>>

    //OTHER
    @Query("SELECT EXISTS(SELECT 1 FROM articles WHERE name = :articleName LIMIT 1)")
    suspend fun existsArticleWithName(articleName: String): Boolean

}