package com.xluis.inventarioefa.data.Database.Room.MovementSelected

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xluis.inventarioefa.data.Model.Room.MovementSelectedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementSelectedDao {
    // INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovementSelected(
        movementSelectedList: List<MovementSelectedEntity>
    ): List<Long>

    @Query("""
    SELECT * FROM movementselected 
    WHERE screenId = :screenId 
    AND articleId = :articleId
    AND zoneId = :zoneId
    LIMIT 1
""")
    suspend fun getMovement(
        screenId: String,
        articleId: String,
        zoneId: String
    ): MovementSelectedEntity?

    @Query("""
        DELETE FROM MovementSelected
        WHERE screenId = :screenId
        AND zoneId = :zoneId
    """)
    suspend fun clearMovementSelectedByScreenAndZone(
        screenId: String,
        zoneId: String
    ): Int

    @Update
    suspend fun updateMovement(movement: MovementSelectedEntity)

    //GET
    @Query("SELECT * FROM MovementSelected WHERE screenId = :screenId AND zoneId = :zoneId")
    fun getMovementsByScreenAndZoneIds(screenId: String, zoneId: String): Flow<List<MovementSelectedEntity>>

    // DELETE

    @Query("DELETE FROM MovementSelected WHERE id IN (:movementIds)")
    suspend fun deleteByMovementIds(
        movementIds: List<String>
    ): Int

    @Query("""
        DELETE FROM MovementSelected
        WHERE articleId IN (:articleIds)
          AND screenId = :screenId
          AND zoneId = :zoneId
    """)
    suspend fun deleteByMovementsIdsAndScreenAndZone(
        articleIds: List<String>,
        screenId: String,
        zoneId: String
    ): Int

    @Query("DELETE FROM MovementSelected")
    suspend fun clearMovementSelected(): Int
}