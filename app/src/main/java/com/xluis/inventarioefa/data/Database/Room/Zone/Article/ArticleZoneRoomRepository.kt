package com.xluis.inventarioefa.domain.model.Database.Room.Article

import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Model.Room.Zone.ArticleZoneEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class ArticleZoneRoomRepository(private val dao: ArticleZoneDao) : BaseRoomRepository() {
    suspend fun insertArticle(newArticle: ArticleZoneEntity): SuspendResult<Boolean> {
        return executeRoomOperation {
            dao.insertArticle(newArticle) > 0
        }
    }

    suspend fun insertArticles(articlesList: List<ArticleZoneEntity>): SuspendResult<Boolean> {
        return executeRoomOperation {
            dao.insertArticles(articlesList)
            true
        }
    }

    suspend fun upsertArticleCount(
        zoneId: Long,
        article: ArticleZoneEntity
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            val existingArticle = dao.getArticleFromZone(article.id, zoneId)

            if (existingArticle == null) {
                dao.insertArticle(
                    article
                ) > 0
            } else {
                dao.addArticleCount(
                    zoneId = zoneId,
                    articleId = article.id,
                    countToAdd = article.count
                ) > 0
            }
        }
    }

    suspend fun deleteArticleDescriptions(
        zoneId: Long,
        articleId: Long,
        descriptionsToRemove: List<String>
    ): SuspendResult<Boolean> {

        if (descriptionsToRemove.isEmpty()) {
            return SuspendResult.Error("La lista de descripciones está vacía")
        }

        return executeRoomOperation {

            val article = dao.getArticleFromZone(articleId, zoneId)
                ?: return@executeRoomOperation false

            val updatedDescriptions = article.descriptions.filterNot { desc ->
                desc in descriptionsToRemove
            }

            val rows = dao.updateArticleDescriptions(
                articleId = articleId,
                zoneId = zoneId,
                descriptions = updatedDescriptions
            )

            rows > 0
        }
    }

    suspend fun upsertArticleCountList(
        zoneId: Long,
        articles: List<ArticleZoneEntity>
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            articles.all { article ->
                val existingArticle = dao.getArticleFromZone(article.id, zoneId)

                if (existingArticle == null) {
                    dao.insertArticle(article) > 0
                } else {
                    dao.addArticleCount(
                        zoneId = zoneId,
                        articleId = article.id,
                        countToAdd = article.count
                    ) > 0
                }
            }
        }
    }



    suspend fun removeArticleCount(
        zoneId: Long,
        articleId: Long,
        countToRemove: Int
    ): SuspendResult<Boolean> {

        return executeRoomOperation {
            val article =
                dao.getArticleFromZone(articleId, zoneId) ?: return@executeRoomOperation false

            val newCount = article.count - countToRemove

            if (shouldDeleteArticle(newCount)) {
                dao.deleteArticle(articleId, zoneId) > 0
            } else {
                dao.substractArticleCount(zoneId, articleId, countToRemove) > 0
            }
        }
    }

    suspend fun addArticleCount(
        zoneId: Long,
        article: ArticleZoneEntity,
        countToAdd: Int
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            // Buscamos el artículo en la zona
            val existingArticle = dao.getArticleFromZone(article.id, zoneId)

            if (existingArticle == null) {
                dao.insertArticle(article)
                true
            } else {
                dao.addArticleCount(zoneId, article.id, countToAdd) > 0
            }
        }
    }


    private fun shouldDeleteArticle(newCount: Int): Boolean {
        return newCount <= 0
    }

    suspend fun removeArticleByIdList(
        zoneId: Long,
        idList: List<Long>
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            dao.deleteArticleByIdList(zoneId, idList) > 0
        }
    }

    suspend fun updateArticleDescription(
        zoneId: Long,
        articleId: Long,
        oldDescription: String,
        newDescription: String
    ): SuspendResult<Boolean> {
        return executeRoomOperation {

            val article = dao.getArticleFromZone(articleId, zoneId)
                ?: return@executeRoomOperation false

            val updatedDescriptions = article.descriptions.map {
                if (it == oldDescription) newDescription else it
            }

            dao.updateArticleDescriptions(
                articleId = articleId,
                zoneId = zoneId,
                descriptions = updatedDescriptions
            ) > 0
        }
    }

    suspend fun getArticleFromZone(
        articleId: Long,
        zoneId: Long
    ): SuspendResult<ArticleZoneEntity?> {
        return executeRoomOperation {
            dao.getArticleFromZone(articleId, zoneId)
        }
    }

    suspend fun updateArticleCount(
        zoneId: Long,
        articleId: Long,
        newCount: Int
    ) : SuspendResult<Int> {
        return executeRoomOperation {
            dao.updateArticleCount(zoneId,articleId,newCount)
        }
    }
}