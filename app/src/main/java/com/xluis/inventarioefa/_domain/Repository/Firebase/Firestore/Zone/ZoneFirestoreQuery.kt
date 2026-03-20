package com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Zone

import com.xluis.inventarioefa._domain.model.DataClass.Result.ValidationResult
import com.xluis.inventarioefa._domain.model.User.ZoneRequest
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa.data.Model.Firestore.Article.ArticleFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Movement.ArticleMovementFirestore
import com.xluis.inventarioefa.data.Model.Firestore.Zone.ZoneFirestore
import com.xluis.inventarioefa.data.Model.Firestore.ZoneFullFirestore
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

interface ZoneFirestoreQuery {

    //Insert
    suspend fun insertZone(zoneFirestore: ZoneFirestore): SuspendResult<Boolean>

    suspend fun createZone(
        zone : ZoneFirestore,
        articleList : List<ArticleFirestore>,
        movementList : List<ArticleMovementFirestore>,
        parentId : String?,
        userId: String
    ) : SuspendResult<Boolean>

    //Update

    //Article And Movements
    suspend fun upsertArticleAndMovementLists(
        zoneId: String,
        articleList: List<ArticleFirestore>,
        movementList: List<ArticleMovementFirestore>
    ): SuspendResult<Boolean>

    //Zones
    suspend fun acceptZoneRequest(
        request: ZoneRequest
    ): SuspendResult<Boolean>

    suspend fun addChildToZone(parentId: String, childToAddId: String): SuspendResult<Boolean>
    suspend fun addChildToParentList(
        parentIdList: List<String>,
        childId: String
    ): SuspendResult<Boolean>

    suspend fun addMember(zoneId: String, newMemberId: String): SuspendResult<Boolean>
    suspend fun removeMember(zoneId: String, memberIdToDelete: String): SuspendResult<Boolean>
    suspend fun insertZoneWithHierarchy(
        zone: ZoneFirestore,
        parentId: String?
    ): SuspendResult<ValidationResult>

    //Article
    suspend fun insertOrUpdateArticle(
        zoneId: String,
        article: ArticleFirestore
    ): SuspendResult<Boolean>

    suspend fun insertMovement(
        zoneId: String,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean>

    suspend fun upsertArticleAndMovement(
        zoneId: String,
        quantityToAdd : Int,
        article: ArticleFirestore,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean>

    //Get
    suspend fun getAllZones(): SuspendResult<List<ZoneFirestore>>

    suspend fun getAllArticleList(zoneId: String): SuspendResult<List<ArticleFirestore>>

    suspend fun getUserArticleMovements(userId : String) : SuspendResult<List<ArticleMovementFirestore>>

    suspend fun getUserZonesSummary (userId: String) : SuspendResult<List<ZoneSummary>>


    suspend fun getUserZones(userId: String): SuspendResult<List<ZoneFirestore>>

    suspend fun getParentIdListByZoneId(zoneId: String): SuspendResult<List<String>>

    suspend fun getRootZonesListByParentIdList(parentList: List<String>): SuspendResult<List<ZoneFirestore>>

    suspend fun getZoneById(zoneId: String): SuspendResult<ZoneFirestore>
    suspend fun getZoneNameById(zoneId: String): SuspendResult<String>
    suspend fun getZoneListByIdList(idList: List<String>): SuspendResult<List<ZoneFirestore>>

    suspend fun getFullZoneById(zoneId: String): SuspendResult<ZoneFullFirestore>

    //Delete

    suspend fun updateOrDeleteArticlesAndMovements(
        zoneId: String,
        articleIdToRemove: String,
        quantityToRemove: Int,
        movement: ArticleMovementFirestore
    ): SuspendResult<Boolean>

    suspend fun insertOrUpdateArticleCount(
        zoneId : String,
        articleToUpdate: ArticleFirestore
    ) : SuspendResult<Boolean>

    suspend fun deleteZoneById(userId : String,zoneId: String): SuspendResult<Boolean>

    suspend fun removeChildId(parentId: String, childId: String): SuspendResult<Boolean>
}