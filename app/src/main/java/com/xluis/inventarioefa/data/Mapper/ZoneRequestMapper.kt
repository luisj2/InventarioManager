package com.xluis.inventarioefa.data.Mapper

import com.google.firebase.Timestamp
import com.xluis.inventarioefa._domain.model.User.ZoneRequest
import com.xluis.inventarioefa.data.Model.User.ZoneRequestFirestore
fun ZoneRequestFirestore.toDomain(): ZoneRequest {
    return ZoneRequest(
        id = this.id,
        zoneId = this.zoneId,
        receiverId = this.receiverId,
        requesterId = this.requesterId,
        createdAt = this.createdAt.toDate()
    )
}

fun ZoneRequest.toFirebase(): ZoneRequestFirestore {
    return ZoneRequestFirestore(
        id = this.id,
        zoneId = this.zoneId,
        requesterId = this.requesterId,
        receiverId = this.receiverId,
        createdAt = Timestamp(this.createdAt)
    )
}

