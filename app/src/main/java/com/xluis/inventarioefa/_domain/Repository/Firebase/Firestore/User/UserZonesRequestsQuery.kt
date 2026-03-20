package com.xluis.inventarioefa._domain.Repository.Firebase.Firestore.User

import com.xluis.inventarioefa.data.Model.Firestore.User.ZoneRequestFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface UserZonesRequestsQuery {

    //INSERT
    suspend fun sendZoneRequestIfNeeded (newRequest : ZoneRequestFirestore) : SuspendResult<Boolean>

    //GET
    suspend fun getUserRequests (userId : String) : SuspendResult<List<ZoneRequestFirestore>>

    //DELETE
    suspend fun deleteUserRequest (userId : String, requestId : String) : SuspendResult<Boolean>
}