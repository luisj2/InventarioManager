package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa.data.Model.Zone.ZoneFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface ZoneFirestoreQuery {

    //Insert
    suspend fun insertZone (zoneFirestore : ZoneFirestore) : SuspendResult<Boolean>

    //Update
    suspend fun addChildToZone(parentId : String,childToAddId : String) : SuspendResult<Boolean>
    suspend fun addChildToParentList (parentIdList : List<String>,childId : String) : SuspendResult<Boolean>
    suspend fun addMember (zoneId : String,newMemberId : String) : SuspendResult<Boolean>
    suspend fun removeMember (zoneId : String, memberIdToDelete : String) : SuspendResult<Boolean>
    suspend fun insertZoneWithHierarchy(zone : ZoneFirestore,parentId : String?) : SuspendResult<ValidationResult>

    //Get
    suspend fun getAllZones () : SuspendResult<List<ZoneFirestore>>

    suspend fun getUserZones (userId : String) : SuspendResult<List<ZoneFirestore>>

    suspend fun getParentIdListByZoneId (zoneId : String) : SuspendResult<List<String>>

    suspend fun getRootZonesListByParentIdList (parentList : List<String>) : SuspendResult<List<ZoneFirestore>>

    suspend fun getZoneById (zoneId : String) : SuspendResult<ZoneFirestore>
    suspend fun getZoneNameById (zoneId : String) : SuspendResult<String>
    suspend fun getZoneListByIdList (idList : List<String>) : SuspendResult<List<ZoneFirestore>>

    //Delete
    suspend fun deleteZoneById (zoneId : String) : SuspendResult<Boolean>

    suspend fun removeChildId (parentId : String,childId : String) : SuspendResult<Boolean>
}