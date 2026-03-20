package com.xluis.inventarioefa.data.Model.Room.Zone

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "article_movements",
    foreignKeys = [
        ForeignKey(
            entity = ZoneEntity::class,
            parentColumns = ["id"],
            childColumns = ["zoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("zoneId"),
        Index("articleId")
    ]
)
data class ArticleMovementsEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var articleId: Long,
    var articleName: String,
    var zoneId: Long,
    var zoneName : String,
    var count: Int,
    var actionType: String,
    var date: LocalDateTime
)
