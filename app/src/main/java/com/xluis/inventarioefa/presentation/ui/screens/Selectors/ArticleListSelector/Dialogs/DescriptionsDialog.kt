package com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector.Dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xluis.inventarioefa._domain.model.Articles.ArticleDescriptionGroup

@Composable
fun DescriptionsDialog(
    showDialog: Boolean,
    groups: List<ArticleDescriptionGroup>,
    onDismiss: () -> Unit,
    onDescriptionChange: (groupIndex: Int, descIndex: Int, value: String) -> Unit,
    onConfirm: () -> Unit
) {

    if (!showDialog) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Añadir descripciones")
        },
        text = {
            Column {

                Text(
                    text = "Rellena las descripciones por artículo:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    itemsIndexed(groups) { groupIndex, group ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {

                            // 🔹 Nombre del artículo
                            Text(
                                text = "${group.article.name} (x${group.article.count})",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // 🔹 Inputs dinámicos según count
                            group.descriptions.forEachIndexed { descIndex, value ->

                                OutlinedTextField(
                                    value = value,
                                    onValueChange = { newValue ->
                                        onDescriptionChange(
                                            groupIndex,
                                            descIndex,
                                            newValue
                                        )
                                    },
                                    label = {
                                        Text("Descripción ${descIndex + 1}")
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}