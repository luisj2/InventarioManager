package com.xluis.inventarioefa.data.Model

import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity

data class ZoneEntitiesBundle (
    val zone : ZoneEntity,
    val articles : List<ArticleZoneEntity>,
    val movements : List<ArticleMovementsEntity>
)
