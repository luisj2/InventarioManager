
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

enum class ArticleCategory(
    val displayName: String,
    val icon: ImageVector
) {
    STORAGE("Almacenamiento", Icons.Default.Inventory2),
    TECHNOLOGY("Tecnología", Icons.Default.Computer),
    VEHICLE("Vehículo", Icons.Default.DirectionsCar),
    LABEL("Etiquetas y señalización", Icons.Default.Label),
    TOOLS("Herramientas", Icons.Default.Build),
    DOCUMENTS("Documentos", Icons.Default.Description),
    PRODUCT("Producto", Icons.Default.ShoppingCart),
    OTHER("Otros", Icons.Default.Category);

    companion object {
        fun fromDisplayName(name: String?): ArticleCategory {
            if (name == null) return OTHER
            return entries.find { it.displayName.equals(name, ignoreCase = true) } ?: OTHER
        }
    }
}

