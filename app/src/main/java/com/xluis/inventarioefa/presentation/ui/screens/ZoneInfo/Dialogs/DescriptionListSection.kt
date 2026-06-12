package com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.Dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun DescriptionListSection(
    show: Boolean,
    onClose: () -> Unit,
    descriptions: List<String>,
    onDeleteDescription: (String) -> Unit,
    onUpdateDescription: (old: String, new: String) -> Unit
) {
    if (!show) return

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Descripciones")

                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                Spacer(Modifier.height(8.dp))

                descriptions.forEach { description ->
                    DescriptionItem(
                        description = description,
                        onDelete = { onDeleteDescription(description) },
                        onUpdate = { new ->
                            onUpdateDescription(description, new)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DescriptionItem(
    description: String,
    onDelete: () -> Unit,
    onUpdate: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var text by remember(description) { mutableStateOf(description) }

    val canDelete = description.isNotBlank() && description != "Sin descripción"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (isEditing) {
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Descripción") },
                    singleLine = true
                )
            } else {
                Text(
                    text = if (description.isBlank()) {
                        "Sin descripción"
                    } else {
                        description
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            IconButton(
                onClick = {
                    if (isEditing && text.isNotBlank()) {
                        onUpdate(text)
                    }
                    isEditing = !isEditing
                }
            ) {
                Icon(
                    imageVector = if (isEditing)
                        Icons.Default.Check
                    else
                        Icons.Default.Edit,
                    contentDescription = null
                )
            }

            // 🔥 SOLO MOSTRAR DELETE SI ES UNA DESCRIPCIÓN REAL
            if (canDelete) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            }
        }
    }
}