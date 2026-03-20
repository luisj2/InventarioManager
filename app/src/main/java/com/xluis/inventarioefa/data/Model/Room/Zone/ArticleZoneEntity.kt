package com.xluis.inventarioefa.data.Model.Room.Zone

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "article",
    primaryKeys = ["id", "zoneId"],
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
    val id: Long = 0,
    val name: String,
    val category: String,
    val zoneId: Long,
    val count: Int = 1
)