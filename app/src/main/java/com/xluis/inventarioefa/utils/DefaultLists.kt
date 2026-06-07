package com.xluis.inventarioefa.utils

import ArticleCategory
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Warning
import com.xluis.inventarioefa.data.Model.Room.ArticleEntity

fun getDefaultArticles(): List<ArticleEntity> {
    return listOf(
        ArticleEntity(
            name = "Caja",
            category = ArticleCategory.STORAGE.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Computadora",
            category = ArticleCategory.TECHNOLOGY.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Carro",
            category = ArticleCategory.VEHICLE.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Etiqueta",
            category = ArticleCategory.LABEL.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Herramienta",
            category = ArticleCategory.TOOLS.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Documento",
            category = ArticleCategory.DOCUMENTS.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Producto",
            category = ArticleCategory.PRODUCT.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Cámara",
            category = ArticleCategory.TECHNOLOGY.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Estante",
            category = ArticleCategory.STORAGE.displayName,
            descriptions = emptyList()
        ),
        ArticleEntity(
            name = "Carpeta",
            category = ArticleCategory.DOCUMENTS.displayName,
            descriptions = emptyList()
        )
    )
}


val INVENTORY_ICON_MAP = mapOf(
    Icons.Default.Home.name to Icons.Default.Home,
    Icons.Default.Place.name to Icons.Default.Place,
    Icons.Default.Store.name to Icons.Default.Store, // sustituto de Warehouse
    Icons.Default.Archive.name to Icons.Default.Archive, // sustituto si Inventory no existe
    Icons.Default.Archive.name to Icons.Default.Archive,
    Icons.Default.Category.name to Icons.Default.Category,
    Icons.Default.Build.name to Icons.Default.Build,
    Icons.Default.Construction.name to Icons.Default.Construction,
    Icons.Default.Handyman.name to Icons.Default.Handyman,
    Icons.Default.Settings.name to Icons.Default.Settings,
    Icons.Default.Handyman.name to Icons.Default.Handyman, // sustituto PanToolAlt
    Icons.Default.ShoppingCart.name to Icons.Default.ShoppingCart,
    Icons.Default.ShoppingBag.name to Icons.Default.ShoppingBag,
    Icons.Default.LocalShipping.name to Icons.Default.LocalShipping,
    Icons.Default.DirectionsCar.name to Icons.Default.DirectionsCar,
    Icons.Default.Folder.name to Icons.Default.Folder,
    Icons.Default.Storage.name to Icons.Default.Storage,
    Icons.Default.Devices.name to Icons.Default.Devices,
    Icons.Default.DesktopWindows.name to Icons.Default.DesktopWindows,
    Icons.Default.TableChart.name to Icons.Default.TableChart,
    Icons.Default.Receipt.name to Icons.Default.Receipt,
    Icons.Default.Assignment.name to Icons.Default.Assignment,
    Icons.Default.Note.name to Icons.Default.Note,
    Icons.Default.Label.name to Icons.Default.Label,
    Icons.Default.BarChart.name to Icons.Default.BarChart,
    Icons.Default.QrCode.name to Icons.Default.QrCode,
    Icons.Default.CameraAlt.name to Icons.Default.CameraAlt,
    Icons.Default.Image.name to Icons.Default.Image,
    Icons.Default.CheckCircle.name to Icons.Default.CheckCircle,
    Icons.Default.Warning.name to Icons.Default.Warning
)


val ARTICLES_ICONS_MAP = mapOf(
    "Archive" to Icons.Default.Archive,
    "DesktopWindows" to Icons.Default.DesktopWindows,
    "DirectionsCar" to Icons.Default.DirectionsCar,
    "Label" to Icons.Default.Label,
    "Build" to Icons.Default.Build,
    "Assignment" to Icons.Default.Assignment,
    "ShoppingCart" to Icons.Default.ShoppingCart,
    "CameraAlt" to Icons.Default.CameraAlt,
    "Storage" to Icons.Default.Storage,
    "Folder" to Icons.Default.Folder
)
