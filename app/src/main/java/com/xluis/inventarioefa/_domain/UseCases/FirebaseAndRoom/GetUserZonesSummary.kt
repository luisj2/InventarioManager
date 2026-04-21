package com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom

import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa._domain.util.getListOrEmpty
import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class GetUserZonesSummary(
    private val userZonesRepository: UserZonesRepository,
    private val zoneRepository: ZoneFirestoreRepository,
    private val zoneRoomRepository: ZoneRoomRepository
) {

    suspend operator fun invoke(userId: String): SuspendResult<List<ZoneSummary>> {

        val summaryList = mutableListOf<ZoneSummary>()

        // 1️⃣ Agregar zonas de Firestore
        summaryList.addAll(zoneRepository.getUserZonesSummary(userId).getListOrEmpty())

        // 2️⃣ Agregar zonas de Room
        zoneRoomRepository.getAllZonesFull(userId).getListOrEmpty().forEach { zoneFull ->
            val zoneId = zoneFull?.zone?.id
            if (zoneId != null) {
                summaryList.add(
                    ZoneSummary(
                        id = zoneId.toString(),
                        name = zoneFull.zone.name
                    )
                )
            }
        }

        // 3️⃣ Retornar resultado acumulado
        return SuspendResult.Success(summaryList)
    }
}

