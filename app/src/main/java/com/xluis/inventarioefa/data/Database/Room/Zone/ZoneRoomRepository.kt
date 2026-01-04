package com.xluis.inventarioefa.data.Database.Room.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Database.Room.InventaryDatabase
import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleMovementDao
import com.xluis.inventarioefa.data.Mapper.toEntitiesBundle
import com.xluis.inventarioefa.data.Model.Room.Relactions.ZoneWithArticlesAndMovements
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleMovementsEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.data.Model.Room.Zone.ZoneEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneDao

class ZoneRoomRepository(
    private val zoneDao: ZoneDao,
    private val movementDao: ArticleMovementDao,
    private val articleZoneDao: ArticleZoneDao,
    private val db: InventaryDatabase
) : BaseRoomRepository() {


    suspend fun insertZone(zone: Zone): SuspendResult<Boolean> {
        val bundle = zone.toEntitiesBundle()

        return executeRoomTransaction(db) {
            zoneDao.insertZone(bundle.zone)

            if (bundle.articles.isNotEmpty()) {
                articleZoneDao.insertArticles(bundle.articles)
            }

            if (bundle.movements.isNotEmpty()) {
                movementDao.insertMovements(bundle.movements)
            }

            true
        }
    }

    suspend fun getZoneFull(zoneId: String): SuspendResult<ZoneWithArticlesAndMovements?> {
        return executeRoomOperation {
            zoneDao.getZoneFull(zoneId)
        }
    }

    suspend fun getAllZonesFull(): SuspendResult<List<ZoneWithArticlesAndMovements?>> {
        return executeRoomOperation {
            zoneDao.getAllZoneFull()
        }
    }

    suspend fun getMovementListByZoneId(zoneId: Long): SuspendResult<List<ArticleMovementsEntity>> {
        return executeRoomOperation {
            movementDao.getMovementListByZoneId(zoneId)
        }
    }


    suspend fun removeZone(zoneId: Long): SuspendResult<Boolean> {
        return executeRoomOperation {
            zoneDao.removeZone(zoneId) > 0
        }
    }

    suspend fun updateZone(zone: ZoneEntity): SuspendResult<Boolean> {
        return executeRoomOperation {
            zoneDao.updateZone(zone) > 0
        }
    }

    suspend fun insertZoneWithHierarchy(
        child: Zone,
        parentId: Long?
    ): SuspendResult<ValidationResult> = executeRoomTransaction(db) {
        // 1️⃣ Convertir la zona y obtener el bundle de entidades
        val bundle = child.toEntitiesBundle()
        val childEntity = bundle.zone

        // 2️⃣ Insertar la zona hija
        val newChildId = zoneDao.insertZone(childEntity)
        if (newChildId <= 0) {
            return@executeRoomTransaction ValidationResult.Error("No se pudo insertar la zona hija")
        }

        // 3️⃣ Si existe padre, actualizar su childIdList
        if (parentId != null) {
            val parentEntity = zoneDao.getZoneById(parentId)
                ?: return@executeRoomTransaction ValidationResult.Error("No se pudo encontrar el padre")

            val updatedChildren = (parentEntity.childIdList ?: emptyList<Long>())
                .toMutableList()
                .apply { add(newChildId) }

            val updatedParent = parentEntity.copy(childIdList = updatedChildren)
            val parentUpdated = zoneDao.updateZone(updatedParent)
            if (parentUpdated <= 0) {
                return@executeRoomTransaction ValidationResult.Error("No se pudo actualizar el padre")
            }
        }

        // 4️⃣ Insertar artículos asociados a esta zona
        if (bundle.articles.isNotEmpty()) {
            val articlesToInsert = bundle.articles.map { it.copy(zoneId = newChildId) }
            articleZoneDao.insertArticles(articlesToInsert)
        }

        // 5️⃣ Insertar movimientos automáticos para cada artículo
        if (bundle.articles.isNotEmpty()) {
            val now = java.time.LocalDateTime.now()
            val movements = bundle.articles.map { article ->
                ArticleMovementsEntity(
                    articleId = article.id.toString(),
                    articleName = article.name,
                    zoneId = newChildId,
                    count = article.count,
                    actionType = "CREATED",
                    zoneName = child.name,
                    date = now
                )
            }
            movementDao.insertMovements(movements)
        }

        ValidationResult.Success
    }

    suspend fun getAllZoneId(): SuspendResult<List<Long>> {
        return executeRoomOperation {
            zoneDao.getAllZoneId()
        }
    }

    suspend fun getAllMovementsByUserZones(): SuspendResult<List<ArticleMovementsEntity>> {
        return executeRoomOperation {
            val zoneIdList = zoneDao.getAllZoneId()
            val allMovements = mutableListOf<ArticleMovementsEntity>()

            for (zoneId in zoneIdList) {
                val movements = movementDao.getMovementListByZoneId(zoneId)
                allMovements.addAll(movements)
            }

            allMovements
        }
    }

    suspend fun getArticleListByZoneId(zoneId: Long): SuspendResult<List<ArticleZoneEntity>> {
        return executeRoomOperation {
            articleZoneDao.getArticleListByZoneId(zoneId)
        }
    }

    suspend fun getZoneNameById (zoneId : Long) : SuspendResult<String>{
        return executeRoomOperation {
            zoneDao.getZoneNameById(zoneId)
        }
    }

}



