package com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleMovementDao {
    //INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement (newMovement : ArticleMovementsEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovements (newMovements : List<ArticleMovementsEntity>)


    //GET
   @Query("SELECT * FROM article_movements WHERE zoneId = :zoneId")
   suspend fun getMovementListByZoneId (zoneId : Long) : List<ArticleMovementsEntity>

    @Query("SELECT * FROM article_movements WHERE zoneId = :zoneId ORDER BY date DESC")
    fun getMovementsListByZoneIdFlow(zoneId: Long): Flow<List<ArticleMovementsEntity>>


    //REMOVE
   @Query("DELETE FROM article_movements WHERE id IN (:idList) AND zoneId = :zoneId")
   suspend fun deleteMovementsByIdList(idList: List<Long>, zoneId: Long): Int





}