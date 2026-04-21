package com.xluis.inventarioefa.presentation.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {


    @Serializable
    data object ArticleMovementsRegister : Screen()

    @Serializable
    data object ZonePrincipal : Screen()

    @Serializable
    data object YourMovements : Screen()

    @Serializable
    data object ZoneRequests : Screen()


    // 🔹 Pantallas secundarias

    @Serializable
    data object MainScreen : Screen()

    @Serializable
    data object MainAuth : Screen()



    @Serializable
    data class ArticleListSelector (val storageType : String,val zoneId : String,val screenId : String) : Screen()

    @Serializable
    data class ZoneSelector(
        val zoneIdFromMove: String,
        val zoneFromMoveStorageType : String,
        val zoneSelectedStorageType : String,
        val articleIdToMove: String,
        val articleCountToMove: Int
    ) : Screen()


    @Serializable
    data class ZoneInfo(val zoneId: String,val storageType : String) : Screen()

    @Serializable
    data class CreateZone(val parentZoneId: String?, val storageType: String) : Screen()



    @Serializable
    data object Settings : Screen()




}
