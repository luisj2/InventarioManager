package com.xluis.inventarioefa.presentation.ui.Navigation

import kotlinx.serialization.Serializable

@Serializable
object MainAuth

@Serializable
object InventoryData

@Serializable
object CreateArticle

@Serializable
object ArticleMovementsRegister

@Serializable
data class ArticleInfo(val articleId: String)

@Serializable
data class TakeItem(val articleId: String)

@Serializable
data class EditArticle (val articleId : String)


