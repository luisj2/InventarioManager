package com.xluis.inventarioefa.domain.model.DataClass.Zone.Filters

import com.xluis.inventarioefa._domain.model.Enums.SortOptionType

enum class ZoneSortOptions(val displayName: String,val type : SortOptionType) {
    CountAsc("Cantidad ↑",SortOptionType.COUNT_ORDER),
    CountDesc("Cantidad ↓",SortOptionType.COUNT_ORDER),
    NameAsc("Nombre A-Z",SortOptionType.NAME),
    NameDesc("Nombre Z-A",SortOptionType.NAME);

    companion object {
        fun fromDisplayName(name: String): ZoneSortOptions =
            entries.find { it.displayName == name } ?: NameAsc
    }
}
