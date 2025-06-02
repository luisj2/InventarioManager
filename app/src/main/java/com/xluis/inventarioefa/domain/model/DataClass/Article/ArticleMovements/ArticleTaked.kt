package com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements

data class ArticleTaked(
    override var id: String? = null,
    override var baseId: String? = null,
    override val articleId: String = "",
    override val articleName: String = "",
    override val userName: String = "",
    override val userId: String = "",
    override val date: String = "",
    override val articleCategory: String = "",
    val articlesTakedCount: Int = 0
) : ArticleMovement(id, articleId, userName, userId, date, articleName, articleCategory) {

    init {
        if (id == null && articleId.isNotBlank() && userId.isNotBlank()) {
            id = "$userId-$articleId-$date"
        }
        if(baseId == null){
            baseId = "$userId-$articleId"
        }
    }
}
