package com.xluis.inventarioefa.data.Database.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xluis.inventarioefa.data.Database.Room.Articles.ArticleDao
import com.xluis.inventarioefa.data.Database.Room.Converters.Converters
import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleMovementDao
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneDao
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneDao

@Database(
    entities = [ArticleZoneEntity::class, ZoneEntity::class, ArticleMovementsEntity::class, ArticleEntity::class],
    version = 8,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class InventaryDatabase : RoomDatabase() {

    abstract fun articleZoneDao(): ArticleZoneDao
    abstract fun zoneDao(): ZoneDao
    abstract fun articleMovementDao(): ArticleMovementDao
    abstract fun articleDao(): ArticleDao
}
