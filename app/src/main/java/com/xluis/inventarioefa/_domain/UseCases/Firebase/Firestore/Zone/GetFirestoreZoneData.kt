package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult


class GetFirestoreZoneData(
    private val zoneRepository: ZoneFirestoreRepository,
    private val articleZoneRepository: ArticleZoneFirestoreRepository,
    private val movementsZoneRepository: ArticleMovementRepository
) {
    suspend operator fun invoke(zoneId: String): SuspendResult<Zone> {
        return zoneRepository.getZoneById(zoneId).flatMap { zoneFirestore ->
            articleZoneRepository.getArticleListByZoneId(zoneId)
                .flatMap { articleListFirestore ->
                    movementsZoneRepository.getMovementsListByZoneId(zoneId)
                        .flatMap { movementListFirestore ->

                            val articles = articleListFirestore.map { it.toDomain() }
                            val movements = movementListFirestore.map { it.toDomain() }

                            SuspendResult.Success(
                                zoneFirestore.toDomain().copy(
                                    articleList = articles,
                                    movementList = movements
                                )
                            )
                        }
                }
        }
    }
}
