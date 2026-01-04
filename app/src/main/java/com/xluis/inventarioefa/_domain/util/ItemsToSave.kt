import androidx.compose.runtime.mutableStateListOf
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article

object ItemsToSave {
    val articles = mutableStateListOf<Article>()
    val movements = mutableStateListOf<ArticleMovement>()

    fun clear() {
        articles.clear()
        movements.clear()
    }
}
