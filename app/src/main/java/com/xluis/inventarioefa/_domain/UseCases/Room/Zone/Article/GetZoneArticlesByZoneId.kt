package com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article

import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.Article.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetZoneArticlesByZoneId (
    private val zoneRepository: ZoneRoomRepository
) {
    suspend operator fun invoke(
        zoneId : Long
    ) : SuspendResult<List<Article>>{
        return when(val result = zoneRepository.getArticleListByZoneId(zoneId)){
            is SuspendResult.Success -> SuspendResult.Success(result.data.map { it.toDomain() })
            is SuspendResult.Error -> result
            else-> SuspendResult.Error("Error Inesperado")
        }
    }
}