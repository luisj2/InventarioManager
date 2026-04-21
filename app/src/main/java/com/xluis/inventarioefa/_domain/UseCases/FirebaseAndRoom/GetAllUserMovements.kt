package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class GetAllUserMovements(
    private val zoneFirestoreRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {

    /**
     * Devuelve un Flow que emite en tiempo real todos los movimientos del usuario
     * combinando Room y Firestore.
     */
    fun observe(userId: String): Flow<List<ArticleMovement>> {

        // 🔹 Flow de Room
        val roomFlow: Flow<List<ArticleMovement>> =
            zoneRoomRepository.getAllMovementsByUserZonesFlow(userId)
                .map { it.map { it.toDomain() } } // Convertimos a domain

        // 🔹 Flow de Firestore
        val firestoreFlow: Flow<List<ArticleMovement>> =
            zoneFirestoreRepository.getUserArticleMovementsFlow(userId)
                .map { it.map { it.toDomain() } } // Convertimos a domain

        // 🔹 Flow padre: combina Room + Firestore
        return combine(roomFlow, firestoreFlow) { roomMovements, firestoreMovements ->
            roomMovements + firestoreMovements
        }
    }
}