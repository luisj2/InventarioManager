package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.PageZoneItems

sealed class ShareZonePages (title : String) : BaseZonePages(title) {
    data object Members : ShareZonePages("Miembros")

    companion object{
        fun pages(): List<BaseZonePages> = listOf(
            Articles,
            Movements,
            Members
        )
    }
}