package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetZoneArticlesbyZoneIdFlow(
    private val zoneRepository: ZoneRoomRepository
) {
    operator fun invoke(
        zoneId: Long
    ): Flow<List<Article>> {
        return zoneRepository.getArticleListByZoneIdFlow(zoneId)
            .map { entities -> entities.map { it.toDomain() } }
    }
}