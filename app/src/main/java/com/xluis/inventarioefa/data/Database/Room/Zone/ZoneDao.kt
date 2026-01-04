package com.xluis.inventarioefa.data.Database.Room.Zone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.xluis.inventarioefa.data.Model.Room.Relactions.ZoneWithArticlesAndMovements
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity

@Dao
interface ZoneDao {

    //INSERT
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(newZone: ZoneEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZones(zonesList: List<ZoneEntity>)

    //GET

    @Query ("SELECT id FROM Zone")
    suspend fun getAllZoneId() : List<Long>

    @Query("SELECT * FROM Zone")
    suspend fun getAllZones(): List<ZoneEntity>

    @Query("SELECT * FROM Zone WHERE id = :zoneId")
    suspend fun getZoneById(zoneId: Long): ZoneEntity

    @Query("SELECT name FROM Zone WHERE id = :zoneId")
    suspend fun getZoneNameById (zoneId : Long) : String

    @Transaction
    @Query("SELECT * FROM Zone WHERE id = :zoneId")
    suspend fun getZoneFull(zoneId: String): ZoneWithArticlesAndMovements?

    @Transaction
    @Query("SELECT * FROM Zone")
    suspend fun getAllZoneFull(): List<ZoneWithArticlesAndMovements?>

    //UPDATE
    @Update
    suspend fun updateZone(zone: ZoneEntity) : Int


    //REMOVE
    @Query("""
        DELETE FROM zone 
        WHERE id = :zoneId
        """
    )
    suspend fun removeZone (zoneId : Long) : Int

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
                childIdList = (parent.childIdList ?: emptyList()) + child.id
            )
            if (updateZone(updatedParent) <= 0) return false

            val updatedChild = child.copy(
                parentIdList = (child.parentIdList ?: emptyList()) + parent.id
            )
            if (updateZone(updatedChild) <= 0) return false
        }

        return true
    }




}