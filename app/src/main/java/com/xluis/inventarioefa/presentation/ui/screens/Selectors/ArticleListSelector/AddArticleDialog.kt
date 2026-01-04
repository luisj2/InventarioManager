package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector

import ArticleCategory
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa.utils.DefaultDropDownSelector

@Composable
fun AddArticleDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddArticle: (Article) -> Unit
) {
    if (!showDialog) return

    var articleName by remember { mutableStateOf("") }
    var articleCategory by remember { mutableStateOf(ArticleCategory.OTHER.displayName) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Añadir nuevo artículo") },
        text = {
            Column {
                OutlinedTextField(
                    value = articleName,
                    onValueChange = { articleName = it },
                    label = { Text("Nombre del artículo") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                DefaultDropDownSelector(
                    optionList = ArticleCategory.entries.map { it.displayName },
                    labelText = "Categoria del Artículo",
                    selectedOption = articleCategory,
                    onOptionSelected = {newCategory-> articleCategory = newCategory}
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                if (articleName.isNotBlank()) {
                    val newArticle = Article(
                        name = articleName,
                        category = ArticleCategory.fromDisplayName(articleCategory)
                    )
                    onAddArticle(newArticle)
                    onDismiss()
                    articleName = ""
                    articleCategory = "1"
                }
            }) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = {
                onDismiss()
            }) {
                Text("Cancelar")
            }
        }
    )
}
