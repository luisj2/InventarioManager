package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo

import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.model.Zone.ZoneMember
import com.xluis.inventarioefa._domain.model.Zone.ZoneSummary
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType

data class ZoneInfoUiState(
    val isLoading: Boolean = false,
    val ownerMember : ZoneMember? = null,
    val memberList : List<ZoneMember> = emptyList(),
    val childZoneSummaryList : List<ZoneSummary> = emptyList(),
    val userId : String? = null,
    val movementList : List<ArticleMovement> = emptyList(),
    val articleList : List<Article> = emptyList(),
    val shareDialogState : Boolean = false,
    val selectionRemoveModeState : Boolean = false,
    val selectionArticleToRemove : List<String> = listOf(),
    val zone: Zone? = null,
    val storageType: StorageType = StorageType.LOCAL,
    val articleToMoveSelected : Article? = null,
    val showConfirmLeftDialog : Boolean = false,
    val showDeleteArticlesDialog : Boolean = false,
    val isFabAddMovementsMenuOpen : Boolean = false,
    )
