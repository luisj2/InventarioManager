package com.xluis.inventarioefa.data.Mapper

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.data.Model.Firestore.Zone.ZoneFirestore
import com.xluis.inventarioefa.data.Model.Firestore.ZoneFullFirestore
import com.xluis.inventarioefa.data.Model.Room.Relactions.ZoneWithArticlesAndMovements
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity
import com.xluis.inventarioefa.data.Model.ZoneEntitiesBundle
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

// ============================================================
// 🔹 De Zone (dominio) a ZoneFirestore (Firestore)
// ============================================================
fun Zone.toFirestore(): ZoneFirestore {
    return ZoneFirestore(
        id = this.id,
        name = this.name,
        ownerId = this.ownerId,
        membersId = this.membersId,
        childIdList = this.childIdList,
        parentIdList = this.parentIdList,
    )
}

// ============================================================
// 🔹 De ZoneFirestore (Firestore) a Zone (dominio)
// ============================================================
fun ZoneFirestore.toDomain(): Zone {
    return Zone(
        id = this.id,
        name = this.name,
        storageType = StorageType.FIREBASE,
        ownerId = this.ownerId,
        membersId = this.membersId,
        childIdList = this.childIdList,
        parentIdList = this.parentIdList,
        articleList = emptyList()
    )
}

// ============================================================
// 🔹 De Zone (dominio) a ZoneEntity (Room)
// ============================================================
fun Zone.toZoneEntity(): ZoneEntity {
    return ZoneEntity(
        id = this.id?.toLongOrNull() ?: 0, // Room requiere PK no nula
        name = this.name,
        userId = this.ownerId ?: "",
        childIdList = this.childIdList?.map { it.toLongOrNull() ?: 0 },
        parentIdList = this.parentIdList?.map { it.toLongOrNull() ?: 0 }
    )
}

// ============================================================
// 🔹 De Zone (dominio) a lista de ArticleZoneEntity (Room)
// ============================================================
fun Zone.toArticleEntities(): List<ArticleZoneEntity> {
    val zoneId = this.id ?: ""
    return this.articleList.map { article ->
        ArticleZoneEntity(
            name = article.name,
            category = article.category.name,
            descriptions = article.descriptions,
            zoneId = zoneId.toLongOrNull() ?: 0,
        )
    }
}

// ============================================================
// 🔹 De Zone (dominio) a lista de ArticleMovementsEntity (Room)
// ============================================================
fun Zone.toMovementEntities(): List<ArticleMovementsEntity> {
    val zoneId = this.id ?: ""
    return this.movementList.map { mov ->
        ArticleMovementsEntity(
            id = 0, // autoGenerate
            articleId = mov.articleId.toLongOrNull() ?: 0,
            articleName = mov.articleName,
            zoneId = zoneId.toLongOrNull() ?: 0,
            count = mov.count,
            actionType = mov.actionType.name,
            zoneName = this.name,
            date = mov.date
        )
    }
}

// ============================================================
// 🔹 Bundle completo de entidades Room a partir de Zone
// ============================================================
fun Zone.toEntitiesBundle(): ZoneEntitiesBundle {
    return ZoneEntitiesBundle(
        zone = this.toZoneEntity(),
        articles = this.toArticleEntities(),
        movements = this.toMovementEntities()
    )
}

fun ZoneWithArticlesAndMovements.toDomain(): Zone {
    return Zone(
        id = this.zone.id.toString(),
        name = this.zone.name,
        storageType = StorageType.LOCAL,
        childIdList = this.zone.childIdList?.map { it.toString() },
        parentIdList = this.zone.parentIdList?.map { it.toString() },
        articleList = this.articles.map { it.toDomain() },
        movementList = this.movements.map { it.toDomain() }
    )
}
fun ZoneFullFirestore.toDomain(): Zone {
    return Zone(
        id = this.zone.id,
        name = this.zone.name ?: "",
        storageType = StorageType.FIREBASE,
        ownerId = this.zone.ownerId,
        membersId = this.zone.membersId ?: emptyList(),
        childIdList = this.zone.childIdList ?: emptyList(),
        parentIdList = this.zone.parentIdList ?: emptyList(),
        articleList = this.articleList.map { it.toDomain() },
        movementList = this.movementList.map { it.toDomain() }
    )
}
