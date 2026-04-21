package com.xluis.inventarioefa.data.Database.Room

import android.content.Context
import androidx.room.Room
import com.xluis.inventarioefa.utils.DATABASE_NAME
import com.xluis.inventarioefa.utils.getDefaultArticles
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object InventoryDatabaseBuilder {

    @Volatile
    private var INSTANCE: InventaryDatabase? = null

    fun getDatabase(context: Context): InventaryDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                InventaryDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()

            // Guardar instancia
            INSTANCE = instance

            CoroutineScope(Dispatchers.IO).launch {
                val dao = instance.articleDao()
                val currentArticles = dao.getAllArticles()
                if (currentArticles.isEmpty()) {
                    dao.insertArticles(getDefaultArticles())
                }
            }

            instance
        }
    }

}
