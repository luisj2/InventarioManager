package com.xluis.inventarioefa.data.Database.Room.Articles

import com.xluis.inventarioefa.data.Database.Room.BaseRoomRepository
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult

class ArticleRepository (
    private val dao : ArticleDao
) : BaseRoomRepository(){

    suspend fun insertArticle (newArticle : ArticleEntity) : SuspendResult<Boolean>{
        return executeRoomOperation {
            dao.insertArticle(newArticle) > 0
        }
    }

    suspend fun insertArticles (articleList : List<ArticleEntity>) : SuspendResult<Boolean>{
        return executeRoomOperation {
            dao.insertArticles(articleList)
            true
        }
    }

    suspend fun getAllArticles () : SuspendResult<List<ArticleEntity>>{
        return executeRoomOperation {
            dao.getAllArticles()
        }
    }
}