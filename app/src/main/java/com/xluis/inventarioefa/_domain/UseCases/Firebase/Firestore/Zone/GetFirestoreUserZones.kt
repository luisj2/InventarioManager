package com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetFirestoreUserZones(
    private val zoneRepository: ZoneFirestoreRepository,
    private val articleZoneRepository: ArticleZoneFirestoreRepository
) {

    suspend operator fun invoke(userId: String): SuspendResult<List<Zone>> {
        return when (val result = zoneRepository.getUserZones(userId)) {
            is SuspendResult.Success -> {
                val zonesResultList = result.data.map { zoneFirestore ->
                    zoneRepository.getZoneById(zoneFirestore.id ?: "")
                        .flatMap { fullZoneFirestore ->
                            articleZoneRepository.getArticleListByZoneId(zoneFirestore.id ?: "")
                                .flatMap { articleListFirestore ->

                                    val articles = articleListFirestore.map { it.toDomain() }

                                    SuspendResult.Success(
                                        fullZoneFirestore.toDomain().copy(
                                            articleList = articles
                                        )
                                    )
                                }
                        }
                }

                // Recopilar solo los éxitos
                val finalZones = zonesResultList.mapNotNull { res ->
                    if (res is SuspendResult.Success) res.data else null
                }

                SuspendResult.Success(finalZones)
            }

            is SuspendResult.Error -> SuspendResult.Error(result.message)
            else -> SuspendResult.Error("Estado inesperado al obtener zonas del usuario")
        }
    }
}
