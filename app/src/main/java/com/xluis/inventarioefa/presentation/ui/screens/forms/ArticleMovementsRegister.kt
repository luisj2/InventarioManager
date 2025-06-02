package com.xluis.inventarioefa.presentation.ui.screens.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.R
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User.UserFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User.UserFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultAdviseMessageDialog
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultMovementCard
import com.xluis.inventarioefa.utils.DefaultMovementReturnCard
import com.xluis.inventarioefa.utils.DefaultTopBar
import com.xluis.inventarioefa.utils.handleErrors
import com.xluis.inventarioefa.utils.toArticleReturned
import com.xluis.inventarioefa.utils.toast

@Composable
fun ArticleMovementsRegisterScreen(
    navigateBack: () -> Unit
) {
    val userViewModel: UserFirestoreViewModel =
        viewModel(factory = UserFirestoreViewModelBuilder.getUserFirestoreViewModelFactory())

    val articleViewModel: ArticleFirestoreViewModel =
        viewModel(factory = ArticleFirestoreViewModelBuilder.getArticleViewModelFactory())

    val authViewModel: AuthViewModel =
        viewModel(factory = AuthViewModelBuilder.getAuthViewModelFactory())

    val userEmail = authViewModel.getLoggedUserEmail()

    val context = LocalContext.current

    val isAdminResult by userViewModel.isAdmin.collectAsState()

    val isUserAdmin = if (isAdminResult is SuspendResult.Success) {
        (isAdminResult as SuspendResult.Success).data
    } else {
        null
    }

    val articleTakedListResult by userViewModel.allArticlesMovementsList.collectAsState()

    val articleMovementsList = if (articleTakedListResult is SuspendResult.Success) {
        (articleTakedListResult as SuspendResult.Success).data
    } else {
        emptyList()
    }

    val addArticleReturnResult by articleViewModel.addArticleReturnStatus.collectAsState()
    val addArticleCountResult by articleViewModel.addArticleCountStatus.collectAsState()
    val userArticleReturnResult by userViewModel.addArticleReturnStatus.collectAsState()
    val userRemoveArticleTakedResult by userViewModel.removeArticleTakedStatus.collectAsState()
    val userSubstractArticleTakedCountResult by userViewModel.substractArticleTakedCountStatus.collectAsState()

    val addArticleReturnStatus = if (addArticleReturnResult is SuspendResult.Success) {
        (addArticleReturnResult as SuspendResult.Success<Boolean>).data
    } else false

    val addArticleCountStatus = if (addArticleCountResult is SuspendResult.Success) {
        (addArticleCountResult as SuspendResult.Success<Boolean>).data
    } else false

    val userArticleReturnStatus = if (userArticleReturnResult is SuspendResult.Success) {
        (userArticleReturnResult as SuspendResult.Success<Boolean>).data
    } else false

    val userRemoveArticleTakedStatus = if (userRemoveArticleTakedResult is SuspendResult.Success) {
        (userRemoveArticleTakedResult as SuspendResult.Success<Boolean>).data
    } else false

    val userSubstractArticleTakedCountStatus =
        if (userSubstractArticleTakedCountResult is SuspendResult.Success) {
            (userSubstractArticleTakedCountResult as SuspendResult.Success<Boolean>).data
        } else false

    val isCorrectlyReturn =
        addArticleReturnStatus && userArticleReturnStatus && addArticleCountStatus
                && (userRemoveArticleTakedStatus || userSubstractArticleTakedCountStatus)

    LaunchedEffect(isCorrectlyReturn) {
        if (isCorrectlyReturn) context.toast("Se ha devuelto correctamente")
    }
    val isLoading =
        addArticleReturnResult is SuspendResult.Loading ||
                addArticleCountResult is SuspendResult.Loading ||
                userArticleReturnResult is SuspendResult.Loading ||
                userRemoveArticleTakedResult is SuspendResult.Loading ||
                userSubstractArticleTakedCountResult is SuspendResult.Loading


    LaunchedEffect(
        addArticleReturnResult,
        addArticleCountResult,
        userArticleReturnResult,
        userRemoveArticleTakedResult,
        userSubstractArticleTakedCountResult
    ) {
        // Manejar errores
        arrayOf(
            addArticleReturnResult,
            addArticleCountResult,
            userArticleReturnResult,
            userRemoveArticleTakedResult,
            userSubstractArticleTakedCountResult,
            articleTakedListResult
        ).handleErrors { message -> context.toast(message) }


    }

    LaunchedEffect(Unit) {
        userViewModel.getIfAdmin(userEmail ?: "")

    }

    LaunchedEffect(isUserAdmin) {
        if (isUserAdmin != null) {
            userViewModel.getAllArticlesMovementsListById(isUserAdmin, userEmail ?: "")

        }
    }
    var showNumberDialog by remember { mutableStateOf(false) }
    var showReturnAllAdviseDialog by remember { mutableStateOf(false) }

    var selectedArticle by remember { mutableStateOf<ArticleTaked?>(null) }

    ManageDialogs(
        selectedArticle = selectedArticle,

        showNumberInputDialog = showNumberDialog,
        showReturnAllArticlesDialog = showReturnAllAdviseDialog,

        onDismissNumberInputDialog = { showNumberDialog = false },
        onDismissReturnAllArticlesDialog = { showReturnAllAdviseDialog = false },

        onConfirmNumberInputDialog = { countSelected ->
            if (selectedArticle == null) {
                context.toast("No se ha podido devolver el Artículo")
                return@ManageDialogs
            }

            val articleReturn = selectedArticle!!.toArticleReturned()
            articleReturn.articlesReturnCount = countSelected

            articleViewModel.addArticleReturn(
                articleId = selectedArticle!!.articleId,
                articleReturn = articleReturn
            )

            articleViewModel.addArticleCountById(
                selectedArticle!!.articleId,
                countSelected
            )


            userViewModel.addArticleReturn(
                userEmail ?: "",
                articleReturn
            )

            if (countSelected >= selectedArticle!!.articlesTakedCount) {
                userViewModel.removeArticleTaked(
                    selectedArticle!!.userId,
                    selectedArticle!!.id ?: ""
                )
            } else {
                userViewModel.substractArticleTakedCount(
                    userEmail ?: "",
                    selectedArticle!!.id ?: "",
                    countSelected
                )
            }

            userViewModel.getAllArticlesMovementsListById(isUserAdmin ?: false, userEmail ?: "")


        },
        onConfirmReturnArticlesDialog = {
            if (selectedArticle == null) {
                context.toast("No se ha podido devolver el Artículo")
                return@ManageDialogs
            }
            val articleReturn = selectedArticle!!.toArticleReturned()

            articleViewModel.addArticleReturn(
                articleId = selectedArticle!!.articleId,
                articleReturn = articleReturn
            )
            articleViewModel.addArticleCountById(
                selectedArticle!!.articleId,
                selectedArticle!!.articlesTakedCount
            )
            userViewModel.removeArticleTaked(selectedArticle!!.userId, selectedArticle!!.id ?: "")
            userViewModel.addArticleReturn(
                authViewModel.getLoggedUserEmail() ?: throw Exception(),
                articleReturn
            )
        }
    )

    ViewScreenContent(
        navigateBack = navigateBack,
        articleMovementsList = articleMovementsList,
        onReturnArticleCount = { articleTaked ->
            showNumberDialog = true
            selectedArticle = articleTaked
        },
        onReturnAllArticles = { articleTaked ->
            showReturnAllAdviseDialog = true
            selectedArticle = articleTaked
        },
        userId = userEmail ?: "",
        isAdmin = isUserAdmin ?: false,
        isRefreshing = isLoading,
        onRefresh = {
            userViewModel.getAllArticlesMovementsListById(isUserAdmin ?: false,userEmail ?: "")
        }
    )

    if (isLoading) DefaultLoadingScreen()


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewScreenContent(
    navigateBack: () -> Unit,
    userId: String,
    isAdmin: Boolean,
    articleMovementsList: List<ArticleMovement>,
    onReturnArticleCount: (ArticleTaked) -> Unit,
    onReturnAllArticles: (ArticleTaked) -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            DefaultTopBar(
                title = "Movimientos",
                backButtonLogic = navigateBack
            )
        }
    ) { padding ->
        PullToRefreshBox(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
            state = pullRefreshState
        ) {
            if (isAdmin) {
                MovementAdminList(
                    userId = userId,
                    list = articleMovementsList,
                    onReturnArticleCount = onReturnArticleCount,
                    onReturnAllArticles = onReturnAllArticles
                )
            } else {
                MovementUserList(
                    list = articleMovementsList,
                    onReturnArticleCount = onReturnArticleCount,
                    onReturnAllArticles = onReturnAllArticles
                )
            }
        }
    }
}



@Composable
private fun ManageDialogs(
    selectedArticle: ArticleTaked?,
    showNumberInputDialog: Boolean,
    showReturnAllArticlesDialog: Boolean,

    onDismissNumberInputDialog: () -> Unit,
    onDismissReturnAllArticlesDialog: () -> Unit,

    onConfirmNumberInputDialog: (Int) -> Unit,
    onConfirmReturnArticlesDialog: () -> Unit
) {
    NumberInputDialog(
        showDialog = showNumberInputDialog,
        onConfirm = { countSelected ->
            onConfirmNumberInputDialog(countSelected)
        },
        onDismiss = onDismissNumberInputDialog,
        range = 1..(selectedArticle?.articlesTakedCount ?: 1)
    )

    DefaultAdviseMessageDialog(
        title = "Devolver todos los artículos de ${selectedArticle?.articleName ?: "este item"}",
        message = "¿Estás seguro de que quieres devolver ${selectedArticle?.articlesTakedCount ?: "todos los articulos"} de ${selectedArticle?.articleName ?: "este item"}",
        confirmButtonMessage = "Sí, devolver todos",
        confirmColor = Color.Red,
        showDialog = showReturnAllArticlesDialog,
        onConfirm = {
            onConfirmReturnArticlesDialog()
        },
        onDismiss = onDismissReturnAllArticlesDialog
    )
}

@Composable
fun MovementUserList(
    modifier: Modifier = Modifier,
    list: List<ArticleMovement>,
    onReturnArticleCount: (ArticleTaked) -> Unit,
    onReturnAllArticles: (ArticleTaked) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { item ->
            when (item) {
                is ArticleReturn -> DefaultMovementCard(
                    icon = R.drawable.return_article_arrow,
                    iconTintColor = Color.Green,
                    title = item.articleName,
                    date = item.date,
                    count = item.articlesReturnCount
                )

                is ArticleTaked -> DefaultMovementReturnCard(
                    title = item.articleName,
                    date = item.date,
                    articleCount = item.articlesTakedCount,
                    onReturnCountClick = { onReturnArticleCount(item) },
                    onReturnAllClick = { onReturnAllArticles(item) }
                )

                else -> {}
            }
        }
    }
}

@Composable
fun MovementAdminList(
    modifier: Modifier = Modifier,
    userId: String,
    list: List<ArticleMovement>,
    onReturnArticleCount: (ArticleTaked) -> Unit,
    onReturnAllArticles: (ArticleTaked) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(list) { item ->
            when (item) {
                is ArticleReturn -> DefaultMovementCard(
                    icon = R.drawable.return_article_arrow,
                    iconTintColor = Color.Green,
                    title = item.articleName,
                    date = item.date,
                    count = item.articlesReturnCount
                )

                is ArticleTaked -> {
                    if (item.userId == userId) {
                        DefaultMovementReturnCard(
                            title = item.articleName,
                            date = item.date,
                            articleCount = item.articlesTakedCount,
                            onReturnCountClick = { onReturnArticleCount(item) },
                            onReturnAllClick = { onReturnAllArticles(item) }
                        )
                    } else {
                        DefaultMovementCard(
                            icon = R.drawable.take_article_arrow,
                            iconTintColor = Color.Red,
                            count = item.articlesTakedCount,
                            title = "${item.userName}-${item.articleName}",
                            date = item.date
                        )
                    }
                }

                else -> {}
            }
        }
    }
}


@Composable
fun NumberInputDialog(
    showDialog: Boolean,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
    range: IntRange = 1..100
) {
    var inputText by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Validación
    val parsedNumber = inputText.toIntOrNull()
    val isValid = parsedNumber != null && parsedNumber in range

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("¿Cuántos quieres devolver?") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                inputText = newValue
                                isError = false
                                errorMessage = ""
                            }
                        },
                        label = { Text("Cantidad (${range.first} a ${range.last})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = isError,
                        singleLine = true
                    )
                    if (isError) {
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (parsedNumber == null) {
                            isError = true
                            errorMessage = "Ingresa un número válido"
                        } else if (parsedNumber !in range) {
                            isError = true
                            errorMessage =
                                "El número debe estar entre ${range.first} y ${range.last}"
                        } else {
                            onConfirm(parsedNumber)
                            onDismiss()
                        }
                    },
                    enabled = isValid
                ) {
                    Text("Confirmar")
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



