package com.xluis.inventarioefa.domain.model.Database.Room.Article

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleZoneDao {

    //INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(newArticle: ArticleZoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articlesList: List<ArticleZoneEntity>) : List<Long>

    //GET
    @Query("""
        SELECT * FROM ARTICLE
        WHERE id = :articleId AND zoneId = :zoneId
        """
        )
    suspend fun getArticleFromZone (articleId : Long,zoneId : Long) : ArticleZoneEntity?


    @Query("""
        SELECT * FROM ARTICLE
        WHERE zoneId = :zoneId
        """)
    suspend fun getArticleListByZoneId (zoneId : Long) : List<ArticleZoneEntity>

    @Query(
        """
        SELECT * FROM ARTICLE
        WHERE zoneId = :zoneId
        """
    )
    fun getArticleListByZoneIdFlow(zoneId: Long): Flow<List<ArticleZoneEntity>>


    //UPDATE

    @Query("""
    UPDATE article
    SET descriptions = :descriptions
    WHERE id = :articleId AND zoneId = :zoneId
""")
    suspend fun updateArticleDescriptions(
        articleId: Long,
        zoneId: Long,
        descriptions: List<String>
    ): Int

    @Query("""
        UPDATE article
        SET count = count - :countToRemove
        WHERE zoneId = :zoneId AND id = :articleId
        """
    )
    suspend fun substractArticleCount (zoneId : Long,articleId : Long, countToRemove : Int) : Int
    @Query("""
        UPDATE article
        SET count = count + :countToAdd
        WHERE zoneId = :zoneId AND id = :articleId
        """
    )
    suspend fun addArticleCount (zoneId : Long,articleId : Long, countToAdd : Int) : Int



    @Query("""
    DELETE FROM article 
    WHERE id = :articleId AND zoneId = :zoneId
""")
    suspend fun deleteArticle(articleId: Long, zoneId: Long): Int


    @Query("""
        DELETE FROM article 
        WHERE id IN (:articleIdList) AND zoneId = :zoneId
    """)
    suspend fun deleteArticleByIdList (zoneId : Long,articleIdList : List<Long>) : Int

    @Query("""
    UPDATE article 
    SET count = :newCount 
    WHERE id = :articleId 
    AND zoneId = :zoneId
""")
    suspend fun updateArticleCount(
        articleId: Long,
        zoneId: Long,
        newCount: Int
    ): Int




}