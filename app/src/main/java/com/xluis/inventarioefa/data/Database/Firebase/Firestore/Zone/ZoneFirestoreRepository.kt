package com.xluis.inventarioefa.data.Database.Firestore.Zone

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.User.ZoneRequest
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa._domain.util.getOrNull
import com.xluis.inventarioefa._domain.util.map
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.BaseFirestoreRepository
import com.xluis.inventarioefa.data.Mapper.toDomain
import com.xluis.inventarioefa.data.Model.Firestore.Article.ArticleFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Zone.ZoneFirestore
import com.xluis.inventarioefa.data.Model.Firestore.ZoneFullFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Zone.ZoneFirestoreQuery
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_COUNT_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ARTICLE_DESCRIPTION_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_MOVEMENTS_ZONEID_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_USER_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_USER_REQUESTS_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_USER_ZONES_LIST_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_ARTICLE_SUBCOLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_COLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONES_MOVEMENTS_SUBCOLLECTION
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_MEMBERS_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_MEMEBERS_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_NAME_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_OWNER_FIELD
import com.xluis.inventarioefa.utils.FIRESTORE_ZONE_PARENT_LIST_FIELD
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ZoneFirestoreRepository(
    private val fs: FirebaseFirestore
) : ZoneFirestoreQuery, BaseFirestoreRepository(fs) {

    // ============================================================
    // 🔹 COLECCIONES Y REFERENCIAS PÚBLICAS
    // ============================================================

    /** Colección principal de zonas */
    fun getZoneCollection(): CollectionReference = fs.collection(FIRESTORE_ZONES_COLLECTION)

    fun getArticleCollection(zoneId: String): CollectionReference =
        fs.collection(FIRESTORE_ZONES_COLLECTION)
            .document(zoneId)
            .collection(FIRESTORE_ZONES_ARTICLE_SUBCOLLECTION)

    private fun getMovementsCollection(zoneId: String): CollectionReference =
        fs.collection(FIRESTORE_ZONES_COLLECTION)
            .document(zoneId)
            .collection(FIRESTORE_ZONES_MOVEMENTS_SUBCOLLECTION)

    private fun getUserCollection() = fs.collection(FIRESTORE_USER_COLLECTION)


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

    override suspend fun createZone(
        zone: ZoneFirestore,
        articleList: List<ArticleFirestore>,
        movementList: List<ArticleMovementFirestore>,
        parentId: String?,
        userId: String
    ): SuspendResult<Boolean> {
        return executeBatchOperation { batch ->
            if (existZoneRequest(
                    userId,
                    zone.id ?: ""
                )
            ) throw Exception("Ya se ha mandado la solicitud de esa zona al usuario")

            val zoneId = zone.id ?: throw Exception("No se ha encontradio la zona")
            insertZoneToBatch(batch, zone)
            insertArticleList(batch, zoneId, articleList)
            insertMovementList(batch, zoneId, movementList)
            if (parentId != null) addChildToZoneToBatch(batch, parentId, zoneId)
            addZoneIdToUserBatch(batch, zoneId, userId)

        }
    }

    private fun insertZoneToBatch(
        batch: WriteBatch,
        zoneFirestore: ZoneFirestore
    ) {
        val zoneRef = getZoneDocumentRef(zoneFirestore.id)
        batch.set(zoneRef, zoneFirestore)
    }


    private fun insertArticleList(
        batch: WriteBatch,
        zoneId: String,
        articleList: List<ArticleFirestore>
    ) {
        val articleCollection = getArticleCollection(zoneId)

        for (article in articleList) {
            val articleId = article.id ?: continue
            val docRef = articleCollection.document(articleId)
            batch.set(docRef, article)
        }

    }

    private fun addChildToZoneToBatch(
        batch: WriteBatch,
        parentId: String,
        childToAddId: String
    ) {
        val parentRef = getZoneDocumentRef(parentId)

        batch.update(
            parentRef,
            FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE,
            FieldValue.arrayUnion(childToAddId)
        )
    }

    private fun insertMovementList(
        batch: WriteBatch,
        zoneId: String,
        movementList: List<ArticleMovementFirestore>
    ) {
        val movementsCollection = getMovementsCollection(zoneId)

        for (movement in movementList) {
            val movementId = movement.id ?: continue
            val docRef = movementsCollection.document(movementId)
            batch.set(docRef, movement)
        }
    }

    private fun addZoneIdToUserBatch(
        batch: WriteBatch,
        zoneId: String,
        userId: String
    ) {
        val userRef = getUserCollection()
            .document(userId)

        batch.update(
            userRef,
            FIRESTORE_USER_ZONES_LIST_FIELD,
            FieldValue.arrayUnion(zoneId)
        )
    }


    override suspend fun upsertArticleAndMovementLists(
        zoneId: String,
        articleList: List<ArticleFirestore>,
        movementList: List<ArticleMovementFirestore>
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            fs.runTransaction { transaction ->

                // 🔹 Artículos
                articleList.forEach { article ->
                    val articleRef = getArticleCollection(zoneId).document(article.id ?: "")
                    val snapshot = transaction.get(articleRef)
                    val existingArticle = snapshot.toObject(ArticleFirestore::class.java)

                    if (existingArticle == null) {
                        transaction.set(articleRef, article)
                    } else {
                        val newCount = (existingArticle.count ?: 0) + (article.count ?: 0)
                        transaction.update(articleRef, FIRESTORE_ARTICLE_COUNT_FIELD, newCount)
                    }
                }

                // 🔹 Movimientos
                movementList.forEach { movement ->
                    val movementRef = getMovementsCollection(zoneId).document(movement.id ?: "")
                    transaction.set(movementRef, movement)
                }

                true
            }.await()
        }
    }

    override suspend fun updateArticleDescription(
        zoneId: String,
        articleId: String,
        oldDescription: String,
        newDescription: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {

            val articleRef = getArticleCollection(zoneId)
                .document(articleId)

            val snapshot = articleRef.get().await()

            val article = snapshot.toObject(ArticleFirestore::class.java)
                ?: throw IllegalStateException("Artículo no existe")

            val currentList = article.descriptions.toMutableList()

            val index = currentList.indexOfFirst { it == oldDescription }

            if (index != -1) {
                currentList[index] = newDescription
            } else {
                currentList.add(newDescription)
            }

            articleRef.update(
                FIRESTORE_ARTICLE_DESCRIPTION_FIELD,
                currentList
            ).await()

            true
        }
    }

    override suspend fun changeZoneName(
        zoneId: String,
        newZoneName: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            val zoneCollection = getZoneCollection()
            val docRef = zoneCollection.document(zoneId)
            docRef.update(FIRESTORE_ZONE_NAME_FIELD,newZoneName).await()
            true
        }
    }


    override suspend fun acceptZoneRequest(request: ZoneRequest): SuspendResult<Boolean> {
        val alreadyMember = withContext(Dispatchers.IO) {
            isUserInZone(request.zoneId, request.receiverId)
        }

        if (alreadyMember) {
            return SuspendResult.Error("El usuario ya es miembro de la zona")
        }

        // 🔹 Si no está, ejecutamos el batch
        return executeBatchOperation { batch ->
            acceptRequest(batch, request)
        }
    }

    private suspend fun acceptRequest(batch: com.google.firebase.firestore.WriteBatch, request: ZoneRequest): Boolean {
        val zoneRef = fs.collection(FIRESTORE_ZONES_COLLECTION).document(request.zoneId)

        // Obtener la lista de hijos
        val zoneSnapshot = zoneRef.get().await()
        val childrenIds =
            zoneSnapshot.get(FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE) as? List<String> ?: emptyList()

        // Añadir al usuario a la zona principal
        batch.update(
            zoneRef,
            FIRESTORE_ZONE_MEMEBERS_FIELD,
            FieldValue.arrayUnion(request.receiverId)
        )

        // Añadir al usuario a cada zona hija
        childrenIds.forEach { childId ->
            val childRef = fs.collection(FIRESTORE_ZONES_COLLECTION).document(childId)
            batch.update(
                childRef,
                FIRESTORE_ZONE_MEMEBERS_FIELD,
                FieldValue.arrayUnion(request.receiverId)
            )
        }

        // Eliminar la solicitud del usuario
        val requesterRequestRef = fs
            .collection(FIRESTORE_USER_COLLECTION)
            .document(request.receiverId)
            .collection(FIRESTORE_USER_REQUESTS_FIELD)
            .document(request.id)

        batch.delete(requesterRequestRef)

        return true
    }

    private suspend fun isUserInZone(zoneId: String, userId: String): Boolean {
        val zoneRef = fs.collection(FIRESTORE_ZONES_COLLECTION).document(zoneId)
        val snapshot = zoneRef.get().await()
        val members = snapshot.get(FIRESTORE_ZONE_MEMEBERS_FIELD) as? List<String> ?: emptyList()
        return members.any { it.equals(userId, ignoreCase = true) }
    }
    private suspend fun existZoneRequest(
        userId: String,
        zoneId: String
    ): Boolean {
        val snapshot = fs
            .collection(FIRESTORE_USER_COLLECTION)
            .document(userId)
            .collection(FIRESTORE_USER_REQUESTS_FIELD)
            .whereEqualTo(FIRESTORE_MOVEMENTS_ZONEID_FIELD, zoneId)
            .limit(1)
            .get()
            .await()

        return !snapshot.isEmpty
    }


    override suspend fun addChildToZone(
        parentId: String,
        childToAddId: String
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(parentId)
                .update(
                    FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE,
                    FieldValue.arrayUnion(childToAddId)
                )
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
                    .update(
                        FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE,
                        FieldValue.arrayUnion(childId)
                    )
                    .await()
            }
            true
        }
    }

    override suspend fun addMember(zoneId: String, newMemberId: String): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getZoneDocumentRef(zoneId)
                .update(FIRESTORE_ZONE_MEMEBERS_FIELD, FieldValue.arrayUnion(newMemberId))
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
                .update(FIRESTORE_ZONE_MEMEBERS_FIELD, FieldValue.arrayRemove(memberIdToDelete))
                .await()
            true
        }
    }

    override suspend fun insertZoneWithHierarchy(
        zone: ZoneFirestore,
        parentId: String?
    ): SuspendResult<ValidationResult> {

        val zoneId = zone.id
            ?: return ValidationResult.Error("El ID de la zona no puede ser nulo")
                .let { SuspendResult.Success(it) }

        return executeBatchOperation { batch ->

            val zoneRef = getZoneDocumentRef(zoneId)

            // 1️⃣ Insertar la zona
            batch.set(zoneRef, zone)

            if (parentId != null) {
                val parentRef = getZoneDocumentRef(parentId)

                // 2️⃣ Añadir hijo al childList del padre
                batch.update(
                    parentRef,
                    FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE,
                    FieldValue.arrayUnion(zoneId)
                )

                // 3️⃣ Actualizar parentIdList de la zona hija
                batch.update(
                    zoneRef,
                    FIRESTORE_ZONE_PARENT_LIST_FIELD,
                    listOf(parentId)
                )
            }
        }.map { ValidationResult.Success }
    }

    override suspend fun insertOrUpdateArticle(
        zoneId: String,
        article: ArticleFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getArticleCollection(zoneId)
                .document(article.id ?: "")
                .set(article)
                .await()
            true
        }
    }

    override suspend fun insertMovement(
        zoneId: String,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {
            getMovementsCollection(zoneId)
                .document(movement.id ?: "")
                .set(movement)
                .await()
            true
        }
    }

    override suspend fun upsertArticleAndMovement(
        zoneId: String,
        quantityToAdd: Int,
        article: ArticleFirestore,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean> {

        return executeFirestoreOperation {

            val articleRef = getArticleCollection(zoneId)
                .document(article.id ?: return@executeFirestoreOperation false)

            val snapshot = articleRef.get().await()

            if (snapshot.exists()) {
                articleRef.update(FIRESTORE_ARTICLE_COUNT_FIELD, FieldValue.increment(quantityToAdd.toLong())).await()
            } else {
                articleRef.set(article.copy(count = quantityToAdd)).await()
            }

            // Insertar movimiento siempre
            getMovementsCollection(zoneId)
                .document(movement.id ?: "")
                .set(movement)
                .await()

            true
        }
    }


    override fun getUserZonesFlow(userId: String): Flow<List<ZoneFirestore>> = callbackFlow {

        var ownerZones: List<ZoneFirestore> = emptyList()
        var memberZones: List<ZoneFirestore> = emptyList()

        fun emitCombined() {
            val combined = (ownerZones + memberZones)
                .distinctBy { it.id } // evitar duplicados
            trySend(combined)
        }

        val ownerListener = getZoneCollection()
            .whereEqualTo(FIRESTORE_ZONE_OWNER_FIELD, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                ownerZones = snapshot?.toObjects(ZoneFirestore::class.java) ?: emptyList()
                emitCombined()
            }

        val memberListener = getZoneCollection()
            .whereArrayContains(FIRESTORE_ZONE_MEMEBERS_FIELD, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                memberZones = snapshot?.toObjects(ZoneFirestore::class.java) ?: emptyList()
                emitCombined()
            }

        awaitClose {
            ownerListener.remove()
            memberListener.remove()
        }
    }


    override suspend fun getAllZones(): SuspendResult<List<ZoneFirestore>> {
        return executeFirestoreOperation {
            getZoneCollection().get()
                .await().documents.mapNotNull { it.toObject(ZoneFirestore::class.java) }
        }
    }

    override suspend fun getAllArticleList(zoneId: String): SuspendResult<List<ArticleFirestore>> {
        return executeFirestoreOperation {
            getArticleCollection(zoneId).get().await().toObjects(ArticleFirestore::class.java)
        }
    }

    override fun getAllArticleListFlow(zoneId: String): Flow<List<ArticleFirestore>> = callbackFlow {
        // Referencia a la colección de artículos de la zona
        val collectionRef = getArticleCollection(zoneId)

        // Listener en tiempo real
        val listenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // cerramos el Flow en caso de error
                return@addSnapshotListener
            }

            val articles = snapshot?.toObjects(ArticleFirestore::class.java) ?: emptyList()
            trySend(articles) // emitimos la lista actualizada
        }

        // Eliminamos el listener cuando el Flow se cancela
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override suspend fun getUserZonesSummary(userId: String): SuspendResult<List<ZoneSummary>> =
        executeFirestoreOperation {

            val summaryList = mutableListOf<ZoneSummary>()
            val seenZoneIds = mutableSetOf<String>()

            // 1️⃣ Agregar zonas explícitas del usuario
            getUserExplicitZones(userId).forEach { zone ->
                if (seenZoneIds.add(zone.id)) summaryList.add(zone)
            }

            // 2️⃣ Agregar zonas donde el usuario es miembro
            getMemberZones(userId).forEach { zone ->
                if (seenZoneIds.add(zone.id)) summaryList.add(zone)
            }

            summaryList
        }

    override suspend fun getUserArticleMovements(userId: String): SuspendResult<List<ArticleMovementFirestore>> =
        executeFirestoreOperation {
            // 🔹 Obtener todas las zonas del usuario
            val userZones = getUserZones(userId).getOrNull() ?: emptyList()
            if (userZones.isEmpty()) return@executeFirestoreOperation emptyList()

            // 🔹 Obtener movimientos de cada zona en paralelo
            coroutineScope {
                val deferredMovements = userZones.map { zone ->
                    async {
                        getMovementsCollection(zone.id ?: "")
                            .get()
                            .await()
                            .toObjects(ArticleMovementFirestore::class.java)
                    }
                }
                // 🔹 Combinar todos los resultados en una sola lista
                deferredMovements.awaitAll().flatten()
            }
        }


// ---------------------- Métodos privados ----------------------

    /** Recupera las zonas explícitas de la lista del usuario */
    private suspend fun getUserExplicitZones(userId: String): List<ZoneSummary> {
        val userDoc = getUserCollection()
            .document(userId)
            .get()
            .await()

        val userZoneIds =
            userDoc.get(FIRESTORE_USER_ZONES_LIST_FIELD) as? List<String> ?: emptyList()

        return userZoneIds.mapNotNull { zoneId ->
            getZoneCollection()
                .document(zoneId)
                .get()
                .await()
                .toObject(ZoneFirestore::class.java)
                ?.toDomain()
                ?.let { zone -> ZoneSummary(id = zone.id!!, name = zone.name) }
        }
    }

    /** Recupera todas las zonas donde el usuario es miembro */


    /** Recupera todas las zonas donde el usuario es miembro */
    private suspend fun getMemberZones(userId: String): List<ZoneSummary> {
        val memberZonesQuery = getZoneCollection()
            .whereArrayContains(FIRESTORE_ZONE_MEMEBERS_FIELD, userId)
            .get()
            .await()

        return memberZonesQuery.documents.mapNotNull { doc ->
            doc.toObject(ZoneFirestore::class.java)
                ?.toDomain()
                ?.let { zone -> ZoneSummary(id = zone.id!!, name = zone.name) }
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

    override fun getZoneByIdFlow(zoneId: String): Flow<ZoneFirestore> = callbackFlow {
        // Obtenemos la referencia al documento de la zona
        val docRef = getZoneDocumentRef(zoneId)

        // Listener de Firestore para cambios en tiempo real
        val listenerRegistration = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // En caso de error cerramos el Flow con excepción
                close(error)
                return@addSnapshotListener
            }

            val zone = snapshot?.toObject(ZoneFirestore::class.java)
            if (zone != null) {
                trySend(zone) // emitimos la zona actualizada
            }
        }

        // Eliminamos el listener cuando se cierra el Flow
        awaitClose {
            listenerRegistration.remove()
        }
    }

    override fun getUserArticleMovementsFlow(userId: String): Flow<List<ArticleMovementFirestore>> = callbackFlow {

        // 🔹 Escucha las zonas donde el usuario es owner
        val ownerListener = getZoneCollection()
            .whereEqualTo(FIRESTORE_ZONE_OWNER_FIELD, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val ownerZones = snapshot?.toObjects(ZoneFirestore::class.java) ?: emptyList()
                ownerZones.forEach { zone ->
                    val zoneId = zone.id ?: return@forEach
                    // Escucha movimientos de esta zona
                    getMovementsCollection(zoneId)
                        .addSnapshotListener { movementSnapshot, movementError ->
                            if (movementError != null) return@addSnapshotListener
                            val movements = movementSnapshot?.toObjects(ArticleMovementFirestore::class.java)
                                ?: emptyList()
                            trySend(movements)
                        }
                }
            }

        // 🔹 Escucha las zonas donde el usuario es miembro
        val memberListener = getZoneCollection()
            .whereArrayContains(FIRESTORE_ZONE_MEMBERS_FIELD, userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                val memberZones = snapshot?.toObjects(ZoneFirestore::class.java) ?: emptyList()
                memberZones.forEach { zone ->
                    val zoneId = zone.id ?: return@forEach
                    // Escucha movimientos de esta zona
                    getMovementsCollection(zoneId)
                        .addSnapshotListener { movementSnapshot, movementError ->
                            if (movementError != null) return@addSnapshotListener
                            val movements = movementSnapshot?.toObjects(ArticleMovementFirestore::class.java)
                                ?: emptyList()
                            trySend(movements)
                        }
                }
            }

        // 🔹 Eliminar listeners cuando se cierre el Flow
        awaitClose {
            ownerListener.remove()
            memberListener.remove()
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

    override suspend fun getFullZoneById(zoneId: String): SuspendResult<ZoneFullFirestore> {
        return executeFirestoreOperation {
            // 1️⃣ Obtener la zona principal
            val zone = getZoneById(zoneId).getOrNull()
                ?: throw IllegalArgumentException("No se ha encontrado la zona")

            // 2️⃣ Obtener artículos y movimientos en paralelo
            val (articles, movements) = coroutineScope {
                val articlesDeferred = async { getArticlesByZoneId(zoneId) }
                val movementsDeferred = async { getMovementsByZoneId(zoneId) }
                articlesDeferred.await() to movementsDeferred.await()
            }

            // 3️⃣ Construir el objeto completo
            ZoneFullFirestore(
                zone = zone,
                articleList = articles,
                movementList = movements
            )
        }
    }


    /** 🔹 Función auxiliar para obtener artículos de una zona */
    private suspend fun getArticlesByZoneId(zoneId: String): List<ArticleFirestore> {
        val snapshot = getArticleCollection(zoneId).get().await()
        return snapshot.toObjects(ArticleFirestore::class.java)
    }

    /** 🔹 Función auxiliar para obtener movimientos de una zona */
    private suspend fun getMovementsByZoneId(zoneId: String): List<ArticleMovementFirestore> {
        val snapshot = getMovementsCollection(zoneId).get().await()
        return snapshot.toObjects(ArticleMovementFirestore::class.java)
    }


    override suspend fun updateOrDeleteArticlesAndMovements(
        zoneId: String,
        articleIdToRemove: String,
        quantityToRemove: Int,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {

            // 1️⃣ Referencia al artículo dentro de la zona
            val articleRef = getArticleCollection(zoneId).document(articleIdToRemove)

            // 2️⃣ Obtener snapshot actual
            val snapshot = articleRef.get().await()
            if (!snapshot.exists()) {
                throw IllegalStateException("El artículo no existe en la zona")
            }

            val article = snapshot.toObject(ArticleFirestore::class.java)
                ?: throw IllegalStateException("Error al mapear el artículo")

            val currentCount = article.count ?: 0
            val newCount = currentCount - quantityToRemove

            // 3️⃣ Update o delete según el nuevo count
            if (newCount > 0) {
                articleRef.update(FIRESTORE_ARTICLE_COUNT_FIELD, newCount).await()
            } else {
                articleRef.delete().await()
            }

            // 4️⃣ Insertar movimiento en la subcolección de la zona
            getMovementsCollection(zoneId)
                .document(movement.id ?: "")
                .set(movement)
                .await()

            true
        }
    }

    override suspend fun insertOrUpdateArticleCount(
        zoneId: String,
        articleToUpdate: ArticleFirestore
    ): SuspendResult<Boolean> {
        return executeFirestoreOperation {

            // 1️⃣ Referencia al artículo dentro de la zona
            val articleRef = getArticleCollection(zoneId)
                .document(articleToUpdate.id ?: return@executeFirestoreOperation false)

            // 2️⃣ Obtener snapshot actual
            val snapshot = articleRef.get().await()

            if (snapshot.exists()) {
                // 🔹 Si existe, hacer SET absoluto del count
                articleRef.update(FIRESTORE_ARTICLE_COUNT_FIELD, articleToUpdate.count).await()
            } else {
                // 🔹 Si no existe, crear documento completo
                articleRef.set(articleToUpdate).await()
            }

            true
        }
    }


    override suspend fun deleteZoneById(
        userId: String,
        zoneId: String
    ): SuspendResult<Boolean> = executeBatchOperation { batch ->

        val zoneRef = getZoneDocumentRef(zoneId)

        val zoneSnapshot = try {
            zoneRef.get().await()
        } catch (e: Exception) {
            throw Exception("No se pudo obtener la zona: ${e.message}")
        } ?: throw Exception("Zona no encontrada")

        // Obtener ownerId
        val ownerId = zoneSnapshot.getString("ownerId")
            ?: throw Exception("No se encontró el owner de la zona")

        // Referencia del usuario que intenta eliminar
        val userRef = fs.collection(FIRESTORE_USER_COLLECTION).document(userId)

        if (userId == ownerId) {
            // 🔹 Si es owner: eliminar la zona completa
            batch.delete(zoneRef)

            // Quitar la zona de todos los miembros
            val members = zoneSnapshot.get("members") as? List<String> ?: emptyList()
            members.forEach { memberId ->
                val memberRef = fs.collection(FIRESTORE_USER_COLLECTION).document(memberId)
                batch.update(memberRef, FIRESTORE_USER_ZONES_LIST_FIELD, FieldValue.arrayRemove(zoneId))
            }

        } else {
            // 🔹 Si es miembro: solo quitar esta zona de su lista
            batch.update(userRef, FIRESTORE_USER_ZONES_LIST_FIELD, FieldValue.arrayRemove(zoneId))
        }
    }

    private suspend fun removeZoneIdInUser(
        userId: String,
        zoneId: String
    ) {
        getUserCollection()
            .document(userId)
            .update(
                FIRESTORE_USER_ZONES_LIST_FIELD,
                FieldValue.arrayRemove(zoneId)
            )
            .await()

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

