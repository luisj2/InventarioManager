package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan


class GetAllZoneList(
    private val zoneRoomRepository: ZoneRoomRepository,
    private val zoneFirestoreRepository: ZoneFirestoreRepository
) {

    operator fun invoke(userId: String): Flow<List<Zone>> {
        return observeAllZones(userId)
    }

    private fun observeAllZones(userId: String): Flow<List<Zone>> {

        // 🔹 Flow de Room (ya reactivo)
        val roomFlow: Flow<List<Zone>> = zoneRoomRepository.getAllZoneFullFlow(userId)
            .map { it.mapNotNull { it?.toDomain() } }

        // 🔹 Flow de Firestore
        val firestoreFlow: Flow<List<Zone>> = zoneFirestoreRepository.getUserZonesFlow(userId)
            .flatMapLatest { firestoreZones ->
                // Cada zona individual se convierte en Flow<List<Zone>>
                val zoneFlows: List<Flow<List<Zone>>> = firestoreZones.mapNotNull { zoneFirestore ->
                    val zoneId = zoneFirestore.id ?: return@mapNotNull null

                    // Flow de la zona + sus artículos
                    zoneFirestoreRepository.getZoneByIdFlow(zoneId)
                        .combine(zoneFirestoreRepository.getAllArticleListFlow(zoneId)) { fullZone, articles ->
                            fullZone?.toDomain()?.copy(
                                articleList = articles.map { it.toDomain() }
                            )
                        }
                        .map { zone -> zone?.let { listOf(it) } ?: emptyList() } // Zone? -> List<Zone>
                }

                if (zoneFlows.isEmpty()) flowOf(emptyList())
                else merge(*zoneFlows.toTypedArray()) // 🔹 merge emite cada cambio individual
                    .scan(emptyList<Zone>()) { acc, value ->
                        // Actualiza incrementalmente la lista
                        val map = acc.associateBy { it.id }.toMutableMap()
                        value.forEach { map[it.id] = it }
                        map.values.toList()
                    }
            }

        // 🔹 Flow padre: combina Room + Firestore y elimina duplicados
        // Combina Room + Firestore y elimina duplicados
        return combine(roomFlow, firestoreFlow) { roomZones, firestoreZones ->
            val allZonesMap = mutableMapOf<String, Zone>()

            // Room: solo zonas con id no nulo
            roomZones.forEach { zone ->
                zone.id?.let { allZonesMap[it] = zone.copy(storageType = StorageType.LOCAL) }
            }

            // Firestore: solo zonas con id no nulo
            firestoreZones.forEach { zone ->
                zone.id?.let { allZonesMap[it] = zone.copy(storageType = StorageType.FIREBASE) } // Firestore sobrescribe si hay duplicados
            }

            allZonesMap.values.toList()
        }
    }
}