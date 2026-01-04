package com.xluis.inventarioefa._domain.model.Enums

enum class SortType(val displayName: String,val sortOptionType: SortOptionType) {

    BY_NAME_ASC("Nombre (A → Z)",SortOptionType.NAME),
    BY_NAME_DESC("Nombre (Z → A)",SortOptionType.NAME),

    BY_COUNT_ASC("Cantidad (menor → mayor)",SortOptionType.COUNT_ORDER),
    BY_COUNT_DESC("Cantidad (mayor → menor)",SortOptionType.COUNT_ORDER);
}
