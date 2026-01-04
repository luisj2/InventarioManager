package com.xluis.inventarioefa.data.Model.Room.Relactions

import androidx.room.Embedded
import androidx.room.Relation
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity

data class ZoneWithArticlesAndMovements(
    @Embedded val zone: ZoneEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "zoneId"
    )
    val articles: List<ArticleZoneEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "zoneId"
    )
    val movements: List<ArticleMovementsEntity>
)
