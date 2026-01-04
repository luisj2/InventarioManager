package com.xluis.inventarioefa.utils.Filters

import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa.domain.model.DataClass.Zone.Filters.ZoneSortOptions

fun filterZones(
    zoneSearchQuery: String,
    filtersList: List<ZoneSortOptions>,
    allZonesList: List<Zone>,
): List<Zone> {

    // 1️⃣ Filtrado por nombre (PADRES + HIJAS)
    val filteredByName = allZonesList.filter { zone ->
        zoneSearchQuery.isBlank() ||
                zone.name.contains(zoneSearchQuery, ignoreCase = true)
    }

     var sorted = filteredByName
    filtersList.forEach { filter ->
        sorted = when (filter) {
            ZoneSortOptions.CountAsc ->
                sorted.sortedBy { it.articleList.size }

            ZoneSortOptions.CountDesc ->
                sorted.sortedByDescending { it.articleList.size }

            ZoneSortOptions.NameAsc ->
                sorted.sortedBy { it.name.lowercase() }

            ZoneSortOptions.NameDesc ->
                sorted.sortedByDescending { it.name.lowercase() }
        }
    }

    return sorted
}
