package com.xluis.inventarioefa.utils

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.xluis.inventarioefa.R
import java.util.Calendar


@Composable
fun DefaultTextField(
    modifier: Modifier = Modifier,
    value: String,
    onTextChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    rounded: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text
) {

    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { newText ->
            onTextChange(newText)
        },
        label = {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(text = label)
            }
        },
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        shape = if (rounded) RoundedCornerShape(16.dp) else TextFieldDefaults.shape,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        )
    )
}

@Composable
fun DefaultAdviseMessageDialog(
    title: String,
    message: String,
    confirmButtonMessage: String,
    confirmColor: Color = Color(0xFF43A047),
    showDialog: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(title) },
            text = { Text(message) },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = confirmColor,
                        contentColor = Color.White
                    )
                ) {
                    Text(confirmButtonMessage)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun DefaultInfoField(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DefaultNumeberPickerWithLabel(
    value: Int,
    onValueChange: (Int) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    range: IntRange = 1..100
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        NumberPicker(
            value = value,
            onValueChange = onValueChange,
            range = range
        )
    }
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    range: IntRange = 1..100
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(
            onClick = {
                if (value > range.first) onValueChange(value - 1)
            }
        ) {
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Decrement")
        }

        TextField(
            value = value.toString(),
            onValueChange = { newText ->
                val number = newText.toIntOrNull()
                if (number != null && number in range) {
                    onValueChange(number)
                } else if (newText.isEmpty()) {
                    onValueChange(range.first)
                }
            },
            modifier = Modifier.width(60.dp),
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
        )

        IconButton(
            onClick = {
                if (value < range.last) onValueChange(value + 1)
            }
        ) {
            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Increment")
        }
    }
}

@Composable
fun DefaultMovementReturnCard(
    modifier: Modifier = Modifier,
    title: String,
    articleCount: Int,
    date: String,
    onReturnCountClick: () -> Unit,
    onReturnAllClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(R.drawable.take_article_arrow),
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "($articleCount)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = date,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.End
            ) {
                IconButton(
                    onClick = onReturnAllClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = Color(0xFF4CAF50), shape = RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.return_article_arrow),
                        contentDescription = "Devolver cantidad",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onReturnCountClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(color = Color(0xFFFF6F61), shape = RoundedCornerShape(4.dp))
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.minus),
                        contentDescription = "Devolver todos",
                        tint = Color.Black
                    )
                }
            }
        }
    }
}


@Composable
fun DefaultMovementCard(
    modifier: Modifier = Modifier,
    icon: Int,
    count: Int,
    iconTintColor: Color = Color.Black,
    title: String,
    date: String
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = iconTintColor,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "($count)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun DefaultDatePicker(
    modifier: Modifier = Modifier,
    label: String = "Seleccionar fecha",
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()


    val date = remember {
        mutableStateOf(
            String.format(
                "%02d-%02d-%04d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR)
            )
        )
    }

    // Llamada inicial
    onDateSelected(date.value)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selected = String.format("%02d-%02d-%04d", day, month + 1, year)
                date.value = selected
                onDateSelected(selected)
            },
            calendar.get(Calendar.DAY_OF_MONTH),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.YEAR)
        )
    }

    Box(
        modifier = modifier
            .clickable { datePickerDialog.show() }
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = date.value.ifBlank { label },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
fun DefaultPasswordTextField(
    modifier: Modifier = Modifier,
    passwordValue: String,
    onPasswordChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = passwordValue,
        onValueChange = { onPasswordChange(it) },
        label = { Text(text = "Contraseña") },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Outlined.Lock
            else
                Icons.Filled.Lock

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, contentDescription = null)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(
    modifier: Modifier = Modifier,
    title: String,
    haveBackButton: Boolean = true,
    backButtonLogic: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = { Text(text = title) },
        navigationIcon = {
            if (haveBackButton) BackButton(backButtonLogic)
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.secondary
        )
    )
}

@Composable
fun DefaultSnackbar(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {

    LaunchedEffect(message, actionLabel) {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel
        )
        if (result == SnackbarResult.ActionPerformed) {
            onAction?.invoke()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}


@Composable
fun DefaultLoadingScreen(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    message: String = "Cargando..."
) {
    if (isVisible) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .zIndex(9999f),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = message,
                    color = Color.Blue
                )
            }
        }
    }
}






@Composable
private fun BackButton(backButtonOnClick: () -> Unit) {
    val context = LocalContext.current
    IconButton(onClick = { backButtonOnClick() }) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultDropDownSelector(
    optionList: List<String>,
    labelText: String,
    onOptionSelected: (String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf("") }


    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = labelText) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.background
            ),
            shape = RoundedCornerShape(16.dp)
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            optionList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOptionText = option
                        onOptionSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }

}

@Composable
fun DefaultButton(
    contentText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        content = { Text(text = contentText) }
    )
}

@Composable
fun DefaultAddImagePreviewButton(
    modifier: Modifier = Modifier,
    onImageChange: (Uri) -> Unit
) {

    val launcher = getUriLauncher(
        onImageSelected = { uri -> onImageChange(uri) }
    )

    val openGalleryCode = "image/*"

    Button(
        modifier = modifier,
        onClick = { launcher.launch(openGalleryCode) }
    ) {
        AddImageButtonContent(modifier = Modifier.padding(horizontal = 20.dp))
    }

}

@Composable
private fun getUriLauncher(onImageSelected: (Uri) -> Unit): ActivityResultLauncher<String> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) onImageSelected(uri)
    }
}

@Composable
private fun AddImageButtonContent(
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier,
        imageVector = Icons.Default.Add,
        contentDescription = "Añadir imagen"
    )

    Spacer(modifier = Modifier.width(8.dp))

    Text(text = "Añadir Imagen")
}

@Composable
fun DefaultImagePreview(
    modifier: Modifier = Modifier,
    adviseMessageText: String,
    imageUri: Uri?
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (imageUri == null) {
            ShowUbicationAdviseText(
                modifier = Modifier.align(Alignment.Center),
                message = adviseMessageText
            )
        } else {
            ChargeImageUri(
                modifier = Modifier.fillMaxHeight(),
                imageUri = imageUri
            )
        }
    }
}

@Composable
private fun ShowUbicationAdviseText(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier,
        text = message,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

}

@Composable
private fun ChargeImageUri(
    modifier: Modifier = Modifier,
    imageUri: Uri
) {
    Image(
        modifier = modifier,
        painter = rememberAsyncImagePainter(model = imageUri),
        contentDescription = "Imagen de la ubicación",
        contentScale = ContentScale.Crop
    )
}

