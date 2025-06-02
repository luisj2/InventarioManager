package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore

import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Ubication
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult


interface UbicationFirestoreQuery {

    //INSERT
    suspend fun insertUbication (ubication : Ubication) : SuspendResult<Boolean>
    //SET

    //DELETE
    suspend fun deleteUbicationById(id : String) : SuspendResult<Boolean>

    //GET
    suspend fun getAllUbications() : SuspendResult<List<Ubication>>
}