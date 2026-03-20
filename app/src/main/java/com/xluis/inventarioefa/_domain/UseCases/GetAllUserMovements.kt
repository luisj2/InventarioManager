package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.util.getListOrEmpty
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetAllUserMovements(
    private val zoneFirestoreRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(
        userId: String
    ): SuspendResult<List<ArticleMovement>> {
        return getDatabaseMovementList(userId)
    }

    private suspend fun getDatabaseMovementList(
        userId: String
    ): SuspendResult<List<ArticleMovement>> {

        val roomMovements = getRoomMovementList()
        val firestoreMovements = getFirestoreArticleMovementList(userId)

        return SuspendResult.Success(
            roomMovements + firestoreMovements
        )
    }


    private suspend fun getRoomMovementList () : List<ArticleMovement>{
        return zoneRoomRepository.getAllMovementsByUserZones().getListOrEmpty().map { it.toDomain() }
    }

    private suspend fun getFirestoreArticleMovementList(
        userId: String
    ): List<ArticleMovement>{
        return zoneFirestoreRepository.getUserArticleMovements(userId).getListOrEmpty().map { it.toDomain() }
    }

}