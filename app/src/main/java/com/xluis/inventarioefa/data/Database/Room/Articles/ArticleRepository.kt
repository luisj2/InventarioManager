package com.xluis.inventarioefa.data.Database.Room.Articles

import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import kotlinx.coroutines.flow.Flow

class ArticleRepository (
    private val dao : ArticleDao
) : BaseRoomRepository(){

    suspend fun insertArticle(newArticle: ArticleEntity): SuspendResult<Boolean> {
        return executeRoomOperation {
            if (!dao.existsArticleWithName(newArticle.name)) {
                dao.insertArticle(newArticle) > 0
            } else {
                throw Exception("Ya existe un artículo con el nombre '${newArticle.name}'")
            }
        }
    }   

    suspend fun insertArticles (articleList : List<ArticleEntity>) : SuspendResult<Boolean>{
        return executeRoomOperation {
            dao.insertArticles(articleList)
            true
        }
    }
    fun getAllArticlesFlow () : Flow<List<ArticleEntity>> = dao.getAllArticlesFlow()

    suspend fun getAllArticles () : SuspendResult<List<ArticleEntity>>{
        return executeRoomOperation {
            dao.getAllArticles()
        }
    }
}