package com.xluis.inventarioefa.data.Model.Firestore.User

import com.google.firebase.Timestamp
import java.util.UUID

data class ZoneRequestFirestore(
    val id : String = UUID.randomUUID().toString(),
    val zoneId: String = "",
    val zoneName : String = "",
    val requesterId: String = "",
    val requesterName : String = "",
    val receiverId : String = "",
    val createdAt: Timestamp = Timestamp.now()
)
