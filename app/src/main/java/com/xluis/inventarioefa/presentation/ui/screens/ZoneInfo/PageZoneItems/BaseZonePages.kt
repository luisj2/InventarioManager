package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.PageZoneItems

sealed class BaseZonePages (val title : String) {
    data object Articles : BaseZonePages("Articulos")
    data object Movements : BaseZonePages("Movimientos")

    companion object{
        fun pages () : List<BaseZonePages> = listOf(Articles,Movements)
    }
}