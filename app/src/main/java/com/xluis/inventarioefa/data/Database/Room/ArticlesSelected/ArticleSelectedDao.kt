package com.xluis.inventarioefa.data.Database.Room.ArticlesSelected

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xluis.inventarioefa.data.Model.Room.ArticleSelectedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleSelectedDao {
    //Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticleSelected (articleSelectedList : List<ArticleSelectedEntity>): List<Long>

    //GET
    @Query ("SELECT * FROM ArticleSelected WHERE screenId = :screenId AND zoneId = :zoneId")
    fun getArticlesByScreenAndZoneIdsFlow(screenId : String, zoneId :String) : Flow<List<ArticleSelectedEntity>>

    @Query ("SELECT * FROM ArticleSelected WHERE screenId = :screenId AND zoneId = :zoneId")
    fun getArticlesByScreenAndZoneIds(screenId : String, zoneId :String) : List<ArticleSelectedEntity>

    @Query("""
        SELECT * FROM ArticleSelected 
        WHERE screenId = :screenId AND articleId = :articleId AND zoneId = :zoneId
        LIMIT 1
    """)
    suspend fun getArticle(screenId: String, articleId: String, zoneId: String): ArticleSelectedEntity?


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArticles(articles: List<ArticleSelectedEntity>)


    //UPDATE
    @Update
    suspend fun updateArticle(article: ArticleSelectedEntity)

    @Update
    suspend fun updateArticles(articles: List<ArticleSelectedEntity>)

    //DELETE

    @Query("DELETE FROM ArticleSelected WHERE articleId IN (:articleIds)")
    suspend fun deleteByArticleIds(articleIds: List<String>) : Int

    @Query("""
    DELETE FROM ArticleSelected
    WHERE articleId IN (:articleIds)
      AND screenId = :screenId
      AND zoneId = :zoneId
""")
    suspend fun deleteByArticleIdsAndScreenAndZone(
        articleIds: List<String>,
        screenId: String,
        zoneId: String
    ): Int
    @Query("DELETE FROM ArticleSelected")
    suspend fun clearArticleSelected() : Int

    @Query("""
        DELETE FROM ArticleSelected
        WHERE screenId = :screenId AND zoneId = :zoneId
    """)
    suspend fun clearArticleSelectedByScreenAndZone(screenId: String, zoneId: String): Int
}