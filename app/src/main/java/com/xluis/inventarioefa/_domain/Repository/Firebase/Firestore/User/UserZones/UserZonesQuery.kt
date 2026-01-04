package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.User.UserZones

import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface UserZonesQuery {
    //Insert
    suspend fun addZoneId (zoneId : String,userId : String) : SuspendResult<Boolean>

    //Get
    suspend fun getZonesIdList (userId : String) : SuspendResult<List<String>>

    suspend fun getLoggedUsername (userId : String): SuspendResult<String>


}