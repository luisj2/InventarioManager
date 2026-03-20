package com.xluis.inventarioefa.data.Model.Room.Zone

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Zone")
data class ZoneEntity(
    @PrimaryKey (autoGenerate = true) val id : Long = 0,
    val userId : String,
    val name : String,
    val childIdList : List<Long>?,
    val parentIdList : List<Long>?
)
