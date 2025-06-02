package com.xluis.inventarioefa.presentation.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.R
import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleMovement
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleReturn
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultMovementCard
import com.xluis.inventarioefa.utils.DefaultTopBar
import com.xluis.inventarioefa.utils.handleErrors
import com.xluis.inventarioefa.utils.parseDate
import com.xluis.inventarioefa.utils.toast

@Composable
fun ArticleInfoScreen(
    articleId: String,
    navigateBack: () -> Unit,
    navigateTakeItem: (String) -> Unit,
    navigateArticleMovementRegister: (String) -> Unit
) {

    val articleFirestoreViewModel: ArticleFirestoreViewModel =
        viewModel(
            factory = ArticleFirestoreViewModelBuilder.getArticleViewModelFactory()
        )

    val authViewModel: AuthViewModel =
        viewModel(
            factory = AuthViewModelBuilder.getAuthViewModelFactory()
        )

    val userEmail = authViewModel.getLoggedUserEmail()

    val articleResult by articleFirestoreViewModel.articleById.collectAsState()
    val articleMovementsResult by articleFirestoreViewModel.allArticleMovementList.collectAsState()

    val isLoading =
        articleResult is SuspendResult.Loading || articleMovementsResult is SuspendResult.Loading

    val context = LocalContext.current

    LaunchedEffect(articleResult, articleMovementsResult) {
        arrayOf(
            articleMovementsResult
        ).handleErrors { message ->
            context.toast(message)
        }
    }

    val articleMovementsList = if (articleMovementsResult is SuspendResult.Success) {
        (articleMovementsResult as SuspendResult.Success).data
    } else {
        emptyList()
    }
    val article = if (articleResult is SuspendResult.Success) {
        (articleResult as SuspendResult.Success).data
    } else {
        null
    }



    LaunchedEffect(Unit) {
        articleFirestoreViewModel.getArticleById(articleId)
        articleFirestoreViewModel.getAllArticleMovementListByArticleId(articleId, userEmail ?: "")
    }

    orderList(
        list = articleMovementsList
    )

    ScreenViewContent(
        article = article,
        navigateBack = navigateBack,
        navigateToTakeItem = navigateTakeItem,
        navigateToArticleMovementsRegister = navigateArticleMovementRegister,
        articleMovementList = articleMovementsList
    )
    if (isLoading) DefaultLoadingScreen()


}

@Composable
private fun ScreenViewContent(
    article: Article?,
    navigateBack: () -> Unit,
    navigateToTakeItem: (String) -> Unit,
    navigateToArticleMovementsRegister: (String) -> Unit,
    articleMovementList: List<ArticleMovement>
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        DefaultTopBar(
            title = article?.name ?: "???",
            backButtonLogic = navigateBack
        )


        ArticleInfoColumn(
            modifier = Modifier
                .padding(horizontal = 8.dp),
            article = article
        )

        MovementTitleAndFunctionsRow(
            navigateToTakeItem = { navigateToTakeItem(article?.id ?: "") },
            navigateToReturnItem = { navigateToArticleMovementsRegister(article?.id ?: "") }
        )

        if (articleMovementList.isEmpty())
            ShowEmptyMessage("No hay movimientos en este articulo")
        else
            ChargeLazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                movementList = articleMovementList
            )
    }
}

fun orderList(list: List<ArticleMovement>): List<ArticleMovement> {
    return list.sortedWith(compareBy(
        {
            when (it) {
                is ArticleTaked -> it.userName
                is ArticleReturn -> it.userName
            }
        },
        {
            when (it) {
                is ArticleTaked -> it.date.parseDate()
                is ArticleReturn -> it.date.parseDate()
            }
        }
    ))
}

@Composable
fun ChargeLazyColumn(
    modifier: Modifier = Modifier,
    movementList: List<ArticleMovement>
) {
    LazyColumn(
        modifier = modifier,
    ) {
        items(movementList) { item ->
            when (item) {
                is ArticleTaked -> {
                    DefaultMovementCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        icon = R.drawable.take_article_arrow,
                        iconTintColor = Color.Red,
                        title = item.userName,
                        date = item.date,
                        count = item.articlesTakedCount
                    )
                }

                is ArticleReturn -> {
                    DefaultMovementCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        icon = R.drawable.return_article_arrow,
                        iconTintColor = Color.Green,
                        title = item.userName,
                        date = item.date,
                        count = item.articlesReturnCount
                    )
                }
            }
        }
    }
}

@Composable
private fun ShowEmptyMessage(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message)
    }
}

@Composable
private fun MovementTitleAndFunctionsRow(
    navigateToTakeItem: () -> Unit,
    navigateToReturnItem: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        MovementTitle(
            modifier = Modifier
                .weight(2f)
                .padding(8.dp)
        )

        IconButtonNavigation(
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            icon = R.drawable.take_article_arrow,
            iconColor = Color.Red,
            navigationLogic = navigateToTakeItem
        )

        IconButtonNavigation(
            modifier = Modifier
                .weight(0.5f)
                .padding(4.dp),
            icon = R.drawable.return_article_arrow,
            iconColor = Color.Green,
            navigationLogic = navigateToReturnItem
        )


    }
}

@Composable
private fun IconButtonNavigation(
    modifier: Modifier = Modifier,
    icon: Int,
    iconColor: Color,
    navigationLogic: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        IconButton(onClick = navigationLogic) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                tint = iconColor
            )
        }
    }
}


@Composable
private fun FilterDropdownWithIcon(
    icon: Int,
    selectedOption: String?,
    options: List<String>,
    onOptionSelected: (String?) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Icon(painter = painterResource(id = icon), contentDescription = null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
private fun MovementTitle(
    modifier: Modifier = Modifier
) {
    val firstHalf = "Movim"
    val secondHalf = "ientos"
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append(firstHalf)
                }
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append(secondHalf)
                }

            },
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun ArticleInfoColumn(
    modifier: Modifier = Modifier,
    article: Article?
) {
    Column(
        modifier = modifier
    ) {
        ItemInfo(
            icon = ImageVector.vectorResource(id = R.drawable.ic_category),
            infoTitle = "Categoría",
            infoValue = article?.category ?: "???"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ItemInfo(
            icon = Icons.Filled.LocationOn,
            infoTitle = "Ubicación",
            infoValue = article?.ubication ?: "???"
        )
        Spacer(modifier = Modifier.height(12.dp))
        ItemInfo(
            icon = ImageVector.vectorResource(id = R.drawable.ic_count),
            infoTitle = "Cantidad",
            infoValue = article?.count.toString()
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ItemInfo(
    icon: ImageVector,
    infoTitle: String,
    infoValue: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        IconCard(icon)
        ItemInfoColumn(
            infoTitle = infoTitle,
            infoValue = infoValue
        )
    }
}

@Composable
private fun ItemInfoColumn(
    infoTitle: String,
    infoValue: String?
) {
    Column(
        modifier = Modifier
            .padding(start = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = infoTitle,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = infoValue ?: "???",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun IconCard(
    icon: ImageVector
) {

    Card(
        modifier = Modifier
            .size(64.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null)
        }
    }
}

private fun handleErrors(
    articleResult: SuspendResult<Article?>,
    articleMovementsResult: SuspendResult<List<ArticleMovement>>,
    showMessage: (String) -> Unit
) {
    if (articleResult is SuspendResult.Error) showMessage(articleResult.message)
    if (articleMovementsResult is SuspendResult.Error) showMessage(articleMovementsResult.message)
}





