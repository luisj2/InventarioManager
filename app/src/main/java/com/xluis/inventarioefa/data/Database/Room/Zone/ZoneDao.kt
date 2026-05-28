package com.xluis.inventarioefa.data.Database.Room.Zone

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.xluis.inventarioefa.data.Model.Room.Relactions.ZoneWithArticlesAndMovements
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {

    // ----------------- INSERT -----------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(newZone: ZoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zonesList: List<ZoneEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement (movement : ArticleMovementsEntity)



    // ----------------- GET -----------------

    @Query("SELECT id FROM Zone")
    suspend fun getAllZoneId(): List<Long>



    @Query("SELECT count FROM article WHERE zoneId = :zoneId AND name = :name LIMIT 1")
    suspend fun getArticleCountByName(zoneId: Long, name: String): Int?


    @Query("SELECT * FROM Zone")
    suspend fun getAllZones(): List<ZoneEntity>

    @Query("SELECT count FROM article WHERE zoneId = :zoneId AND name = :articleName")
    suspend fun getArticleCountByIdAndName(articleName: String, zoneId : Long) : Int?

    @Query("SELECT count FROM article WHERE zoneId = :zoneId AND id = :articleId")
    suspend fun getArticleCountById(zoneId: Long, articleId: Long): Int?
    @Query("SELECT * FROM article WHERE zoneId = :zoneId")
    suspend fun getArticlesById(zoneId : Long) : List<ArticleZoneEntity>


    @Query("SELECT * FROM article")
    suspend fun getAllArticles() : List<ArticleZoneEntity>


    @Query("SELECT * FROM Zone WHERE id = :zoneId")
    suspend fun getZoneById(zoneId: Long): ZoneEntity?

    @Query("SELECT name FROM Zone WHERE id = :zoneId")
    suspend fun getZoneNameById(zoneId: Long): String?
    @Query("""
        SELECT am.* 
        FROM article_movements AS am
        INNER JOIN Zone AS z ON am.zoneId = z.id
        WHERE z.userId = :userId
    """)
    fun getAllMovementsByUserZonesFlow(userId: String): Flow<List<ArticleMovementsEntity>>

    @Transaction
    @Query("SELECT * FROM Zone WHERE id = :zoneId")
    suspend fun getZoneFull(zoneId: Long): ZoneWithArticlesAndMovements?

    @Transaction
    @Query("SELECT * FROM Zone WHERE userId = :userId OR userId = ''")
    suspend fun getAllZoneFull(userId : String): List<ZoneWithArticlesAndMovements>

    @Transaction
    @Query("""
    SELECT * 
    FROM Zone
    WHERE userId = :userId
       OR userId = ''
""")    fun getAllZoneFullFlow(userId: String): Flow<List<ZoneWithArticlesAndMovements>>


    // ----------------- UPDATE -----------------

    @Update
    suspend fun updateZone(zone: ZoneEntity): Int

    @Query(
        """
UPDATE article
SET count = :newCount
WHERE zoneId = :zoneId AND id = :articleId
"""
    )
    suspend fun updateArticleCount(
        zoneId: Long,
        articleId: Long,
        newCount: Int
    ): Int

    @Query("""
    UPDATE article
    SET count = :newCount
    WHERE zoneId = :zoneId AND id = :articleId
""")
    suspend fun updateArticleCountById(
        zoneId: Long,
        articleId : Long,
        newCount: Int
    ): Int

    @Query("""
    UPDATE Zone
    SET name = :newZoneName
    WHERE id = :zoneId
""")
    suspend fun changeZoneName (zoneId : Long,newZoneName : String) : Int


    @Update
    suspend fun updateZones(zones: List<ZoneEntity>): Int




    // ----------------- DELETE -----------------

    @Query("DELETE FROM Zone WHERE id = :zoneId")
    suspend fun removeZone(zoneId: Long): Int

    @Delete
    suspend fun deleteZones(zones: List<ZoneEntity>): Int


    // ----------------- ZONE HIERARCHY -----------------

    @Transaction
    suspend fun insertZoneWithHierarchy(
        child: ZoneEntity,
        parentId: Long?
    ): Boolean {
        val insertedId = insertZone(child)
        if (insertedId <= 0) return false

        if (parentId != null) {
            val parent = getZoneById(parentId) ?: return false

            val updatedParent = parent.copy(
                childIdList = (parent.childIdList ?: emptyList()) + insertedId
            )
            if (updateZone(updatedParent) <= 0) return false

            val updatedChild = child.copy(
                id = insertedId,
                parentIdList = (child.parentIdList ?: emptyList()) + parent.id
            )
            if (updateZone(updatedChild) <= 0) return false
        }

        return true
    }


    // ----------------- ARTICLES -----------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleZoneEntity>)

    @Update
    suspend fun updateArticles(articles: List<ArticleZoneEntity>): Int

    @Delete
    suspend fun deleteArticles(articles: List<ArticleZoneEntity>): Int

    @Query("DELETE FROM article WHERE zoneId = :zoneId AND id = :articleId")
    suspend fun deleteArticleById(zoneId: Long, articleId: Long): Int

    @Query("DELETE FROM article WHERE zoneId = :zoneId")
    suspend fun deleteArticlesByZone(zoneId: Long): Int


    // ----------------- MOVEMENTS -----------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovements(movements: List<ArticleMovementsEntity>)

    @Update
    suspend fun updateMovements(movements: List<ArticleMovementsEntity>): Int

    @Delete
    suspend fun deleteMovements(movements: List<ArticleMovementsEntity>): Int

    @Query("DELETE FROM article_movements WHERE zoneId = :zoneId")
    suspend fun deleteMovementsByZone(zoneId: Long): Int


    // ----------------- TRANSACTIONS -----------------

    @Transaction
    suspend fun upsertArticlesAndMovements(
        zoneId : Long,
        articles: List<ArticleZoneEntity>,
        movements: List<ArticleMovementsEntity>
    ) {
        insertOrUpsertArticleCountList(zoneId,articles)
        insertMovements(movements)
    }

    @Transaction
    suspend fun upsertArticleAndMovement(
        zoneId : Long,
        article: ArticleZoneEntity,
        movement: ArticleMovementsEntity
    ) {
        // Insertar o actualizar el artículo
        insertOrUpsertArticleCount(zoneId,article)
        // Insertar o actualizar el movimiento
        insertMovement(movement)
    }



    @Transaction
    suspend fun insertOrUpsertArticleCount(
        zoneId: Long,
        article: ArticleZoneEntity
    ) {
        // 1️⃣ Asegurarnos de que el artículo tiene el zoneId correcto
        val articleWithZone = article.copy(zoneId = zoneId)

        // 2️⃣ Obtener el count actual en esta zona
        val currentCount = getArticleCountById(zoneId, articleWithZone.id)

        if (currentCount != null) {
            // 3️⃣ Si existe, actualizar sumando la cantidad
            val newCount = currentCount + articleWithZone.count
            updateArticleCountById(zoneId, articleWithZone.id, newCount)
        } else {
            // 4️⃣ Si no existe, insertar el artículo completo
            insertArticles(listOf(articleWithZone))
        }
    }
    @Transaction
    suspend fun insertOrUpdateArticleCount(zoneId :Long, article: ArticleZoneEntity) {
        // Intentar obtener el count actual
        val currentCount = getArticleCountByIdAndName(article.name,zoneId)

        if (currentCount != null) {
            // Si existe, actualizar count sumando el nuevo
            updateArticleCount(articleId = article.id, zoneId = zoneId, newCount = article.count)
        } else {
            // Si no existe, insertar el artículo completo
            insertArticles(listOf(article))
        }
    }
    @Transaction
    suspend fun insertOrUpsertArticleCountList(
        zoneId : Long,
        articles: List<ArticleZoneEntity>
    ) {
        articles.forEach { article ->
            // 1️⃣ Obtener el count actual
            val currentCount = getArticleCountById(zoneId,article.id)
            if (currentCount != null) {
                updateArticleCountById(zoneId, article.id, currentCount + article.count)
            } else {
                // 3️⃣ Si no existe → insertar
                insertArticles(listOf(article))
            }
        }
    }
    @Transaction
    suspend fun insertOrUpdateArticleCountList(
        zoneId : Long,
        articles: List<ArticleZoneEntity>
    ) {
        articles.forEach { article ->
            // 1️⃣ Obtener el count actual
            val currentCount = getArticleCountById(zoneId,article.id)
            if (currentCount != null) {
                updateArticleCountById(zoneId, article.id,article.count)
            } else {
                insertArticles(listOf(article))
            }
        }
    }



    @Transaction
    suspend fun updateOrDeleteArticlesAndMovements(
        zoneId : Long,
        articleId: Long,
        quantityToRemove: Int,
        movement: ArticleMovementsEntity
    ) {
        // 1️⃣ Obtener el count actual
        val currentArticleCount = getArticleCountById(zoneId,articleId)

        if (currentArticleCount != null) {
            // 2️⃣ Calcular la nueva cantidad
            val newCount = currentArticleCount - quantityToRemove

            // 3️⃣ Update o Delete según corresponda
            if (newCount > 0) {
                updateArticleCount(zoneId,articleId, newCount)
            } else {
                deleteArticleById(zoneId, articleId)
            }
        }

        // 4️⃣ Insertar el movimiento
        insertMovement(movement)
    }


}
