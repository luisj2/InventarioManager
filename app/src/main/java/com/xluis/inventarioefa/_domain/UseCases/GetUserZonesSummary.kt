package com.xluis.inventarioefa._domain.UseCases

import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa._domain.util.flatMap
import com.xluis.inventarioefa._domain.util.getListOrEmpty
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserZonesSummary(
    private val userZonesRepository: UserZonesRepository,
    private val zoneRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {

    suspend operator fun invoke(userId: String): SuspendResult<List<ZoneSummary>> {

        val summaryList = mutableListOf<ZoneSummary>()

        return userZonesRepository.getZonesIdList(userId).flatMap { zonesIds ->

            // 1️⃣ Agregar zonas de Firestore al summaryList
            zonesIds.forEach { zoneId ->
                zoneRepository.getZoneById(zoneId).getOrNull()?.toDomain()?.let { zone ->
                    zone.id?.let { summaryList.add(ZoneSummary(id = it, name = zone.name)) }
                }
            }

            // 2️⃣ Agregar zonas de Room al summaryList
            zoneRoomRepository.getAllZonesFull().getListOrEmpty().forEach { zoneFull ->
                val zoneId = zoneFull?.zone?.id
                summaryList.add(
                    ZoneSummary(
                        id = zoneId.toString(),
                        name = zoneFull?.zone?.name ?: ""
                    )
                )
            }

            // 3️⃣ Retornar resultado acumulado como SuspendResult
            SuspendResult.Success(summaryList)
        }
    }
}
