package com.xluis.inventarioefa.data.Database.Room.ArticlesSelected

import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleSelectedEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.flow.Flow

class ArticleSelectedRepository(
    private val dao: ArticleSelectedDao
) : BaseRoomRepository() {

    suspend fun insertArticles(
        articles: List<ArticleSelectedEntity>
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            if (articles.isEmpty()) throw Exception("No hay ningun articulo que añadir")
            dao.insertArticleSelected(articles).isNotEmpty()
        }
    }

    suspend fun insertOrUpdateArticles(
        articles: List<ArticleSelectedEntity>
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            if (articles.isEmpty()) throw Exception("No hay artículos que añadir")

            val screenId = articles.first().screenId
            val zoneId = articles.first().zoneId


            val existingArticles = dao.getArticlesByScreenAndZoneIds(screenId, zoneId)
            val existingMap = existingArticles.associateBy { it.articleId }

            // 3️⃣ Preparar listas para update e insert
            val toUpdate = mutableListOf<ArticleSelectedEntity>()
            val toInsert = mutableListOf<ArticleSelectedEntity>()

            articles.forEach { article ->
                val existing = existingMap[article.articleId]
                if (existing != null) {
                    toUpdate.add(existing.copy(count = existing.count + article.count))
                } else {
                    toInsert.add(article)
                }
            }

            if (toUpdate.isNotEmpty()) dao.updateArticles(toUpdate)
            if (toInsert.isNotEmpty()) dao.insertArticles(toInsert)

            true
        }
    }
    fun getArticlesByScreenAndZoneIds(
        screenId : String,
        zoneId : String
    ) : Flow<List<ArticleSelectedEntity>> {
        return dao.getArticlesByScreenAndZoneIdsFlow(screenId,zoneId)
    }

    suspend fun deleteByArticleIds(
        articleIds: List<String>
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            if (articleIds.isEmpty()) throw Exception("No hay ningun articulo que eliminar")
            dao.deleteByArticleIds(articleIds) > 0
        }
    }

    suspend fun deleteArticlesByScreenAndZone(
        articleIds: List<String>,
        screenId: String,
        zoneId: String
    ): SuspendResult<Boolean> {
        return executeRoomOperation {
            if (articleIds.isEmpty()) throw Exception("No hay ningún artículo que eliminar")
            dao.deleteByArticleIdsAndScreenAndZone(articleIds, screenId, zoneId) > 0
        }
    }

    suspend fun clearAll(): SuspendResult<Boolean> {
        return executeRoomOperation {
            dao.clearArticleSelected() > 0
        }
    }
    suspend fun clearAllByScreenAndZone(screenId: String, zoneId: String): SuspendResult<Boolean> {
        return executeRoomOperation {
            val rowsDeleted = dao.clearArticleSelectedByScreenAndZone(screenId, zoneId)
            rowsDeleted > 0
        }
    }
}