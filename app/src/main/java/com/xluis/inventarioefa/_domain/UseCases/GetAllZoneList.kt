package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.onError
import com.xluis.inventarioefa._domain.util.onSuccess
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetAllZoneList(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    suspend operator fun invoke(userId: String): SuspendResult<List<Zone>> {
        val allZones = mutableListOf<Zone>()

        // Firestore
        val firestoreResult = zoneFirestoreRepository.getUserZones(userId)
        firestoreResult.onSuccess { zones ->
            zones.forEach { zoneFirestore ->
                val zoneId = zoneFirestore.id ?: return@forEach
                val fullZoneResult = zoneFirestoreRepository.getZoneById(zoneId)
                val articlesResult = zoneFirestoreRepository.getAllArticleList(zoneId)

                fullZoneResult.onSuccess { fullZone ->
                    articlesResult.onSuccess { articles ->
                        val zone = fullZone.toDomain().copy(
                            articleList = articles.map { it.toDomain() }
                        )
                        allZones.add(zone)
                    }.onError { errorMessage ->
                        return SuspendResult.Error("Error obteniendo artículos: $errorMessage")
                    }
                }.onError { errorMessage ->
                    return SuspendResult.Error("Error obteniendo zona completa: $errorMessage")
                }
            }
        }.onError { errorMessage ->
            return SuspendResult.Error("Error obteniendo zonas de Firestore: $errorMessage")
        }

        // Room
        val roomResult = zoneRoomRepository.getAllZonesFull(userId)
        roomResult.onSuccess { zones ->
            val roomZones = zones.mapNotNull { it?.toDomain() }
            allZones.addAll(roomZones)
        }.onError { errorMessage ->
            return SuspendResult.Error("Error obteniendo zonas de Room: $errorMessage")
        }

        return SuspendResult.Success(allZones)
    }
}