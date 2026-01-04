package com.xluis.inventarioefa.data.Database.Firestore.Zone

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.util.fold
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Model.Zone.ZoneFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Zone.ZoneFirestoreQuery
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_MEMEBERS_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_NAME_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_OWNER_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_PARENT_LIST_FIELD
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await


class ZoneFirestoreRepository(
    private val fs: FirebaseFirestore
) : ZoneFirestoreQuery, BaseFirestoreRepository(fs) {

    // ============================================================
    // 🔹 COLECCIONES Y REFERENCIAS PÚBLICAS
    // ============================================================

    /** Colección principal de zonas */
    fun getZoneCollection(): CollectionReference = fs.collection(FIRESTORE_ZONES_COLLECTION)

    /** Referencia a un documento de zona por ID */
    fun getZoneDocumentRef(zoneId: String?): DocumentReference {
        val id = zoneId ?: throw IllegalStateException("El ID de la zona no puede ser nulo")
        return getZoneCollection().document(id)
    }

    /** Lista de referencias para batch */
    fun buildZoneReferencesList(zoneList: List<ZoneFirestore>): List<Pair<DocumentReference, ZoneFirestore>> {
        val collection = getZoneCollection()
        return zoneList.mapNotNull { zone ->
            zone.id?.let { id ->
                collection.document(id) to zone
            }
        }
    }



    override suspend fun insertZone(zoneFirestore: ZoneFirestore): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(zoneFirestore.id).set(zoneFirestore).await()
            true
        }
    }



    override suspend fun addChildToZone(
        parentId: String,
        childToAddId: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(parentId)
                .update(FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE, FieldValue.arrayUnion(childToAddId))
                .await()
            true
        }
    }

    override suspend fun addChildToParentList(
        parentIdList: List<String>,
        childId: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            parentIdList.forEach { parentId ->
                getZoneDocumentRef(parentId)
                    .update(FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE, FieldValue.arrayUnion(childId))
                    .await()
            }
            true
        }
    }

    override suspend fun addMember(zoneId : String,newMemberId: String): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(zoneId)
                .update(FIRESTORE_ZONE_MEMEBERS_FIELD,FieldValue.arrayUnion(newMemberId))
                .await()
            true
        }
    }

    override suspend fun removeMember(
        zoneId: String,
        memberIdToDelete: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(zoneId)
                .update(FIRESTORE_ZONE_MEMEBERS_FIELD,FieldValue.arrayRemove(memberIdToDelete))
                .await()
            true
        }
    }

    override suspend fun insertZoneWithHierarchy(
        zone: ZoneFirestore,
        parentId: String?
    ): SuspendResult<ValidationResult> {
        return executeFirestoreOperation {
            val zoneId = zone.id
                ?: return@executeFirestoreOperation ValidationResult.Error("El ID de la zona no puede ser nulo")

            // 1️⃣ Insertar la zona
            val zoneResult = insertZone(zone).fold(
                onSuccess = { ValidationResult.Success },
                onError = { msg -> ValidationResult.Error("Error al guardar la zona: $msg") }
            )
            if (zoneResult is ValidationResult.Error) return@executeFirestoreOperation zoneResult

            // 2️⃣ Si existe padre → añadir hijo al childList del padre
            if (parentId != null) {
                val parentResult = addChildToZone(
                    parentId = parentId,
                    childToAddId = zoneId
                ).fold(
                    onSuccess = { ValidationResult.Success },
                    onError = { msg ->
                        // rollback zona si falla
                        deleteZoneById(zoneId)
                        ValidationResult.Error("Error al añadir hijo al padre: $msg")
                    }
                )
                if (parentResult is ValidationResult.Error) return@executeFirestoreOperation parentResult
            }

            // 3️⃣ Actualizar parentIdList de la zona hija
            if (parentId != null) {
                getZoneDocumentRef(zoneId)
                    .update(FIRESTORE_ZONE_PARENT_LIST_FIELD, listOf(parentId))
                    .await()
            }

            ValidationResult.Success
        }
    }










    override suspend fun getAllZones(): SuspendResult<List<ZoneFirestore>> {
        return executeFirestoreOperation {
            getZoneCollection().get().await().documents.mapNotNull { it.toObject(ZoneFirestore::class.java) }
        }
    }

    override suspend fun getUserZones(userId: String): SuspendResult<List<ZoneFirestore>> {
        return executeFirestoreOperation {

            val ownerQuery = getZoneCollection()
                .whereEqualTo(FIRESTORE_ZONE_OWNER_FIELD, userId)
                .get()
                .await()
                .toObjects(ZoneFirestore::class.java)

            val memberQuery = getZoneCollection()
                .whereArrayContains(FIRESTORE_ZONE_MEMEBERS_FIELD, userId)
                .get()
                .await()
                .toObjects(ZoneFirestore::class.java)

            val combined = (ownerQuery + memberQuery)
                .distinctBy { it.id }

            combined
        }
    }


    override suspend fun getParentIdListByZoneId(zoneId: String): SuspendResult<List<String>> {
        return executeFirestoreOperation {
            val snapshot = getZoneDocumentRef(zoneId).get().await()
            if (!snapshot.exists()) return@executeFirestoreOperation emptyList()
            snapshot.get(FIRESTORE_ZONE_PARENT_LIST_FIELD) as? List<String> ?: emptyList()
        }
    }

    override suspend fun getRootZonesListByParentIdList(
        parentList: List<String>
    ): SuspendResult<List<ZoneFirestore>> {
        return executeFirestoreOperation {
            if (parentList.isEmpty()) return@executeFirestoreOperation emptyList()
            val querySnapshot = getZoneCollection()
                .whereIn(FieldPath.documentId(), parentList)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(ZoneFirestore::class.java)?.apply { id = doc.id }
            }.filter { zone ->
                zone.parentIdList?.isEmpty() ?: true
            }
        }
    }

    override suspend fun getZoneById(zoneId: String): SuspendResult<ZoneFirestore> {
        return executeFirestoreOperation {
            getZoneDocumentRef(zoneId).get().await().toObject(ZoneFirestore::class.java)
                ?: throw IllegalArgumentException("No se encontró la zona con id: $zoneId")
        }
    }

    override suspend fun getZoneNameById(zoneId: String): SuspendResult<String> {
        return executeFirestoreOperation {

            val snapshot = getZoneDocumentRef(zoneId).get().await()

            if (!snapshot.exists()) {
                return@executeFirestoreOperation ""
            }

            snapshot.getString(FIRESTORE_ZONE_NAME_FIELD).orEmpty()
        }
    }



    override suspend fun getZoneListByIdList(idList: List<String>): SuspendResult<List<ZoneFirestore>> {
        return executeFirestoreOperation {
            if (idList.isEmpty()) return@executeFirestoreOperation emptyList()

            coroutineScope {
                val deferredZones = idList.map { id ->
                    async {
                        getZoneDocumentRef(id).get().await().toObject(ZoneFirestore::class.java)
                    }
                }
                deferredZones.awaitAll().filterNotNull()
            }
        }
    }

    override suspend fun deleteZoneById(zoneId: String): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(zoneId).delete().await()
            true
        }
    }

    override suspend fun removeChildId(parentId: String, childId: String): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(parentId)
                .update(FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE, FieldValue.arrayRemove(childId))
                .await()
            true
        }
    }

}

