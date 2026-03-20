package com.xluis.inventarioefa.utils

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.xluis.inventarioefa.R
import com.xluis.inventarioefa._domain.model.DataClass.ArticleMovement
import com.xluis.inventarioefa._domain.model.DataClass.Articles.Article
import com.xluis.inventarioefa._domain.model.DataClass.Zone.Zone
import com.xluis.inventarioefa._domain.util.toReadable
import com.xluis.inventarioefa.domain.model.DataClass.Enums.MovementAction
import com.xluis.inventarioefa.domain.model.DataClass.Zone.Filters.ZoneSortOptions
import com.xluis.inventarioefa.domain.model.DataClass.Zone.StorageType
import com.xluis.inventarioefa.presentation.ui.theme.GreenPrimary
import com.xluis.inventarioefa.presentation.ui.theme.WhiteSecundary
import java.util.Calendar


@Composable
fun FullScreenLoadingOverlay(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(enabled = true, onClick = {})
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 5.dp
            )
        }
    }
}


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
fun ValidatedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onTextChange: (String) -> Unit,
    label: String,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    rounded: Boolean = false,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = modifier
    ) {
        TextField(
            value = value,
            onValueChange = { newText -> onTextChange(newText) },
            label = { Text(text = label) },
            singleLine = singleLine,
            minLines = minLines,
            maxLines = maxLines,
            isError = isError,
            shape = if (rounded) RoundedCornerShape(16.dp) else TextFieldDefaults.shape,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType),
            modifier = Modifier.fillMaxWidth()
        )

        // 🔹 Muestra el mensaje de error debajo
        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .padding(start = 16.dp, top = 4.dp)
            )
        }
    }
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

@Composable
fun ValidatedPasswordTextField(
    modifier: Modifier = Modifier,
    passwordValue: String,
    onPasswordChange: (String) -> Unit,
    label: String = "Contraseña",
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        TextField(
            value = passwordValue,
            onValueChange = { onPasswordChange(it) },
            label = { Text(text = label) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else
                    Icons.Filled.VisibilityOff

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = isError,
            modifier = Modifier.fillMaxWidth()
        )

        if (isError && !errorMessage.isNullOrBlank()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultTopBar(
    modifier: Modifier = Modifier,
    title: String,
    haveBackButton: Boolean = false,
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
    IconButton(onClick = { backButtonOnClick() }) {
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultDropDownSelector(
    optionList: List<String>,
    labelText: String,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(),
            value = selectedOption,
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
                        onOptionSelected(option)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconDropDownSelector(
    icon: ImageVector = Icons.Default.MoreVert,
    optionList: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    dropdownTextColor: Color = Color.Black
) {
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        IconButton(onClick = { isExpanded = true }) {
            Icon(
                imageVector = icon,
                contentDescription = "Abrir menú",
                tint = Color.Black
            )
        }

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier,
        ) {
            optionList.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = dropdownTextColor) },
                    onClick = {
                        onOptionSelected(option)
                        isExpanded = false
                    },
                    colors = MenuDefaults.itemColors(
                        textColor = dropdownTextColor
                    )
                )
            }
        }
    }
}


@Composable
fun DefaultButton(
    contentText: String,
    containerColor: Color = GreenPrimary,
    textColor: Color = WhiteSecundary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text = contentText)
    }
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

@Composable
fun CurvedBorderBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Parte superior con curva
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.75f)
                cubicTo(
                    size.width * 0.25f, size.height * 1.1f,
                    size.width * 0.75f, size.height * 0.4f,
                    size.width, size.height * 0.8f
                )
                lineTo(size.width, 0f)
                lineTo(0f, 0f)
                close()
            }
            drawPath(path, GreenPrimary)
        }

        // Parte inferior con otra curva
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .height(200.dp)
        ) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.25f)
                cubicTo(
                    size.width * 0.3f, size.height * -0.2f,
                    size.width * 0.7f, size.height * 1.3f,
                    size.width, size.height * 0.9f
                )
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }
            drawPath(path, GreenPrimary.copy(alpha = 0.9f))
        }

        // Contenido encima
        content()
    }
}


@Composable
fun DefaultSearchBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChanged: (String) -> Unit,
    placeholderText: String = "Buscar...",
    placeholderColor: Color = Color.Black,
    leadingIcon: ImageVector = Icons.Default.Search
) {
    val searchQuery = remember { mutableStateOf(query) }

    Box(modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
        OutlinedTextField(
            value = searchQuery.value,
            onValueChange = { newValue ->
                searchQuery.value = newValue
                onQueryChanged(newValue)
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = "Buscar"
                )
            },
            placeholder = {
                Text(
                    text = placeholderText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = placeholderColor
                )
            },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = Color.Black
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

@Composable
fun IconPickerDialog(
    icons: List<ImageVector>,
    onIconSelected: (ImageVector) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.6f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                Text(
                    text = "Selecciona un icono",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(icons) { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable {
                                    onIconSelected(icon)
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleItem(
    article: Article,
    isArticleSelected: Boolean = false,
    selectedColor: Color = Color.Blue,
    unselectedColor: Color = Color.Gray,
    showCount: Boolean = true,
    onDelete: (() -> Unit)? = null,
    onSelectArticle: ((count: Int) -> Unit)? = null,
    onLongPress: ((Article) -> Unit)? = null
) {

    var count by remember { mutableIntStateOf(article.count.coerceAtLeast(1)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .combinedClickable(
                onClick = { onSelectArticle?.invoke(count) },
                onLongClick = { onLongPress?.invoke(article) }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isArticleSelected) selectedColor else unselectedColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = article.category.icon,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = article.name,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (showCount) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Button(
                        onClick = {
                            if (count > 1) {
                                count--
                                if (isArticleSelected) onSelectArticle?.invoke(count)
                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("-", color = Color.White)
                    }

                    Text(
                        text = count.toString(),
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color.Black
                    )

                    Button(
                        onClick = {
                            count++
                            if (isArticleSelected) onSelectArticle?.invoke(count)
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("+", color = Color.White)
                    }

                    if (onDelete != null) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ArticleList(
    modifier: Modifier = Modifier,
    articles: List<Article>,
    selectedArticle: Article? = null,
    onArticleSelected: ((article: Article) -> Unit)? = null,
    showCount: Boolean = true,
    onLongPress: ((Article) -> Unit)? = null
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(articles) { article ->

            ArticleItem(
                article = article,
                isArticleSelected = selectedArticle?.id == article.id,
                onSelectArticle = { count ->
                    onArticleSelected?.invoke(article.copy(count = count))
                },
                showCount = showCount,
                onLongPress = onLongPress
            )
        }
    }
}


@Composable
fun MovementsList(
    modifier: Modifier = Modifier,
    movementsList: List<ArticleMovement>
    ) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(movementsList) { movement ->
            MovementItem(movement = movement)
        }
    }
}


@Composable
fun MovementItem(
    movement: ArticleMovement,
) {
    val (actionText, actionColor, actionIcon) = when (movement.actionType) {
        MovementAction.TAKE -> Triple("Tomado", Color.Red, Icons.Default.ArrowDownward)
        MovementAction.ADD -> Triple("Añadido", Color.Blue, Icons.Default.Add)
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F7F7)),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = actionIcon,
                contentDescription = actionText,
                tint = actionColor,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(28.dp)
            )

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "$actionText: ${movement.count} unidad${if (movement.count > 1) "es" else ""}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(2.dp))

                if(movement.zoneName.isNotBlank()){
                    Text(
                        text = "Zona: ${movement.zoneName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF444444)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Artículo: ${movement.articleName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Realizado por: ${movement.userName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = movement.date.toReadable(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}


@Composable
fun SearchAndFiltersBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    filters: List<ZoneSortOptions>,
    onFilterAdded: (ZoneSortOptions) -> Unit,
    onFilterRemoved: (ZoneSortOptions) -> Unit,
    searchPlaceholder: String = "Buscar zona...",
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        // Barra de búsqueda + selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DefaultSearchBar(
                modifier = Modifier.weight(1f),
                query = query,
                onQueryChanged = onQueryChanged,
                placeholderColor = Color.White,
                placeholderText = searchPlaceholder
            )

            IconDropDownSelector(
                optionList = ZoneSortOptions.entries.map { it.displayName },
                onOptionSelected = { option ->
                    onFilterAdded(ZoneSortOptions.fromDisplayName(option))
                }
            )
        }

        if (filters.isNotEmpty()) {
            ActiveFiltersRow(
                filterList = filters,
                onRemoveFilter = onFilterRemoved
            )
        }
    }
}

@Composable
fun ZoneList(
    zones: List<Zone>,
    allZonesList: List<Zone>,
    selectedZones: List<Zone> = emptyList(),
    onZoneClick: ((Zone) -> Unit)? = null,
    onZoneLongClick: ((Zone) -> Unit)? = null,
    onAddSubzoneClick: ((parentId: String, storageType: StorageType) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(zones) { zone ->
            ZoneTree(
                zone = zone,
                allZonesList = allZonesList,
                navigateToCreateZone = onAddSubzoneClick,
                onZoneClick = onZoneClick ?: {},
                selectedZones = selectedZones,
                onZoneLongClick = onZoneLongClick
            )
        }
    }
}

@Composable
fun ZoneRefreshList(
    zones: List<Zone>,
    allZonesList: List<Zone>,
    selectedZones: List<Zone>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onZoneClick: ((Zone) -> Unit)? = null,
    onZoneLongClick: ((Zone) -> Unit)? = null,
    onAddSubzoneClick: ((parentId: String, storageType: StorageType) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    PullToRefreshLazyColumn(
        items = zones,
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier
    ) { zone ->

        ZoneTree(
            zone = zone,
            allZonesList = allZonesList,
            navigateToCreateZone = onAddSubzoneClick,
            onZoneClick = onZoneClick ?: {},
            selectedZones = selectedZones,
            onZoneLongClick = onZoneLongClick
        )
    }
}


// Row de filtros activos
@Composable
private fun ActiveFiltersRow(
    filterList: List<ZoneSortOptions>,
    onRemoveFilter: (ZoneSortOptions) -> Unit,
    modifier: Modifier = Modifier
) {
    if (filterList.isNotEmpty()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filterList.forEach { filter ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.height(32.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = filter.displayName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        IconButton(
                            onClick = { onRemoveFilter(filter) },
                            modifier = Modifier.size(20.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar filtro",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

// Árbol de zonas reutilizable
@Composable
private fun ZoneTree(
    zone: Zone,
    allZonesList: List<Zone>,
    navigateToCreateZone: ((parentId: String, storageType: StorageType) -> Unit)? = null,
    onZoneClick: (zone: Zone) -> Unit,
    onZoneLongClick: ((zone: Zone) -> Unit)?,
    selectedZones: List<Zone>,
    level: Int = 0
) {
    var isExpanded by remember { mutableStateOf(false) }

    val childZones = remember(zone, allZonesList) {
        allZonesList.filter { zone.childIdList?.contains(it.id) == true }
    }

    ZoneItem(
        zone = zone,
        onZoneClick = onZoneClick,
        level = level,
        hasChildren = childZones.isNotEmpty(),
        onAddSubzoneClick = navigateToCreateZone,
        onExpandClick = { isExpanded = !isExpanded },
        selectedZones = selectedZones,
        modifier = Modifier.padding(start = (level * 20).dp),
        onZoneLongClick = onZoneLongClick
    )

    if (isExpanded) {
        childZones.forEach { child ->
            ZoneTree(
                zone = child,
                allZonesList = allZonesList,
                navigateToCreateZone = navigateToCreateZone,
                onZoneClick = onZoneClick,
                selectedZones = selectedZones,
                level = level + 1,
                onZoneLongClick = onZoneLongClick
            )
        }
    }
}

@Composable
fun SimpleZoneSelector(
    zones: List<Zone>,
    selectedZoneIds: List<String> = emptyList(),
    onZoneClick: (Zone) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(zones) { zone ->
            SimpleZoneItem(
                zone = zone,
                isSelected = selectedZoneIds.contains(zone.id),
                onClick = { onZoneClick(zone) }
            )
        }
    }
}

@Composable
fun SimpleZoneItem(
    zone: Zone,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFD1E8FF) else Color(0xFFF9F9F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icono según tipo de almacenamiento
            val typeIcon = when(zone.storageType) {
                StorageType.LOCAL -> Icons.Default.Lock
                StorageType.FIREBASE -> Icons.Default.Group
            }

            Icon(
                imageVector = typeIcon,
                contentDescription = if(zone.storageType == StorageType.LOCAL) "Privada" else "Compartida",
                tint = if(zone.storageType == StorageType.LOCAL) Color.Red else Color.Green,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(20.dp)
            )

            // Nombre de la zona
            Text(
                text = zone.name,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) Color(0xFF0D47A1) else Color(0xFF212121),
                modifier = Modifier.weight(1f)
            )

            // Icono de seleccionado
            AnimatedVisibility(visible = isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckBox,
                    contentDescription = "Seleccionado",
                    tint = Color(0xFF0D47A1)
                )
            }
        }
    }
}


// Elemento individual de zona
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ZoneItem(
    zone: Zone,
    onZoneClick: (zone: Zone) -> Unit,
    onZoneLongClick: ((zone: Zone) -> Unit)? = null,
    level: Int = 0,
    hasChildren: Boolean,
    modifier: Modifier = Modifier,
    selectedZones: List<Zone> = emptyList(),
    onAddSubzoneClick: ((parentId: String, storageType: StorageType) -> Unit)? = null,
    onExpandClick: (() -> Unit)? = null
) {

    val cardColor = when {
        zone in selectedZones -> Color(0xFFBBDEFB) // seleccionado
        zone.storageType == StorageType.LOCAL -> Color(0xFFE8F5E9) // verde claro
        zone.storageType == StorageType.FIREBASE -> Color(0xFFFFF3E0) // naranja claro
        else -> getZoneCardColor(level)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .combinedClickable(
                onClick = { onZoneClick(zone) },
                onLongClick = { onZoneLongClick?.invoke(zone) }
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (hasChildren) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Expandir zona",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 4.dp)
                            .clickable { onExpandClick?.invoke() }
                    )
                }

                Column {
                    Text(
                        text = zone.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = if (zone.articleList.size == 1) "1 artículo" else "${zone.articleList.size} artículos",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // Botón opcional de subzona
            if (onAddSubzoneClick != null) {
                Button(
                    onClick = { onAddSubzoneClick(zone.id ?: "", zone.storageType) },
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Subzona")
                }
            }
        }
    }
}


// Color de tarjeta según nivel
fun getZoneCardColor(level: Int): Color {
    val baseGray = 0xFFB0BEC5
    val alphaStep = 0.1f
    val factor = (level * alphaStep).coerceAtMost(0.5f)
    return Color(baseGray).copy(alpha = 1f - factor)
}









