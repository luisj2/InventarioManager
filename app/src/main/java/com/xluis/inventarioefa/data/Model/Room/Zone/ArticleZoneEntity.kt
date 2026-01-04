package com.xluis.inventarioefa.data.Model.Room.Zone

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "article",
    foreignKeys = [
        ForeignKey(
            entity = ZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["zoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("zoneId")]
)
data class ArticleZoneEntity(
    @PrimaryKey (autoGenerate = true) val id: Long = 0,
    val name: String,
    val category: String,
    val zoneId: Long,
    val count : Int = 1
)
