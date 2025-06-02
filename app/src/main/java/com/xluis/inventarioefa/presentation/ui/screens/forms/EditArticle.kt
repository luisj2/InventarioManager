package com.xluis.inventarioefa.presentation.ui.screens.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Category
import com.xluis.inventarioefa.domain.model.DataClass.Article.Enums.Ubication
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.Result.UiEvent
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication.UbicationFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication.UbicationFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultTextField
import com.xluis.inventarioefa.utils.DefaultTopBar
import com.xluis.inventarioefa.utils.handleErrors
import com.xluis.inventarioefa.utils.toast

@Composable
fun EditArticleScreen(
    articleId: String,
    navigateBack: () -> Unit
) {
    val articleViewModel: ArticleFirestoreViewModel = viewModel(
        factory = ArticleFirestoreViewModelBuilder.getArticleViewModelFactory()
    )

    val ubicationViewModel: UbicationFirestoreViewModel = viewModel(
        factory = UbicationFirestoreViewModelBuilder.getUbicationFirestoreViewModelFactory()
    )

    val context = LocalContext.current

    val articleResult by articleViewModel.articleById.collectAsState()
    val ubicationListResult by ubicationViewModel.allUbicationsList.collectAsState()
    val updateArticleByIdResult by articleViewModel.articleById.collectAsState()

    val updateSuccess = updateArticleByIdResult is SuspendResult.Success

    LaunchedEffect(updateSuccess){
        if(updateSuccess) context.toast("Articulo actualizado correctamente")
    }

    val article = when (articleResult) {
        is SuspendResult.Success -> (articleResult as SuspendResult.Success).data
        else -> null
    }

    val ubicationList = when (ubicationListResult) {
        is SuspendResult.Success -> (ubicationListResult as SuspendResult.Success).data
        else -> emptyList<Ubication>()
    }


    LaunchedEffect(articleResult,ubicationListResult){
        arrayOf(
            articleResult,
            ubicationListResult
        ).handleErrors { message->
            context.toast(message)
        }
    }


    //Loading
    val articleLoading = articleResult is SuspendResult.Loading
    val ubicationLoading = ubicationListResult is SuspendResult.Loading

    val isLoading = articleLoading || ubicationLoading

    LaunchedEffect(Unit) {
        articleViewModel.getArticleById(articleId)
        ubicationViewModel.getAllUbications()
    }


        ViewScreenContent(
            article = article,
            ubicationList = ubicationList,
            navigateBack = navigateBack,
            onSaveArticleChanges = { editedArticle ->
                articleViewModel.updateArticleById(
                    articleId,
                    editedArticle ?: article!!.copy()
                )
            }
        )

    if (isLoading) DefaultLoadingScreen()

}

private fun handleUiEvents(event: UiEvent, onShowMessage: (String) -> Unit) {
    when (event) {
        is UiEvent.ShowToast -> onShowMessage(event.message)
        else -> {}
    }
}

@Composable
private fun ViewScreenContent(
    article: Article?,
    ubicationList: List<Ubication>,
    navigateBack: () -> Unit,
    onSaveArticleChanges: (Article?) -> Unit
) {
    var editedArticle by remember { mutableStateOf<Article?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        DefaultTopBar(
            title = "Editar Artículo",
            backButtonLogic = navigateBack
        )

        EditArticleFields(
            modifier = Modifier.fillMaxWidth(),
            article = article,
            ubicationList = ubicationList.map { it.name },
            onArticleChange = { editedArticle = it }
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp),
            contentText = "Guardar Cambios",
            onClick = {
                if (!isValidArticle(editedArticle)) {
                    context.toast("Completa todos los campos obligatorios")
                    return@DefaultButton
                }
                if (article!! == editedArticle) {
                    context.toast("Cambia algun valor")
                    return@DefaultButton
                }
                onSaveArticleChanges(editedArticle)
            }
        )
    }
}


private fun isValidArticle(article: Article?): Boolean {
    if (article == null) return false

    return (article.name.isNotBlank()
            && !article.category.isNullOrBlank()
            && !article.ubication.isNullOrBlank())
}


@Composable
fun EditArticleFields(
    modifier: Modifier = Modifier,
    article: Article?,
    ubicationList: List<String>,
    onArticleChange: (Article) -> Unit
) {
    val context = LocalContext.current

    var articleName by remember { mutableStateOf("") }
    var articleCategory by remember { mutableStateOf("") }
    var articleUbication by remember { mutableStateOf("") }
    var articleDescription by remember { mutableStateOf("") }
    var articleCount by remember { mutableIntStateOf(1) }
    var articleState by remember { mutableStateOf("") }

    LaunchedEffect(article) {
        article?.let {
            articleName = it.name
            articleCategory = it.category ?: ""
            articleUbication = it.ubication ?: ""
            articleDescription = it.description
            articleCount = it.count
            articleState = it.state ?: ""
        }
    }

    LaunchedEffect(
        articleName,
        articleCategory,
        articleUbication,
        articleDescription,
        articleCount,
        articleState
    ) {
        if (article != null) {
            onArticleChange(
                article.copy(
                    name = articleName,
                    category = articleCategory,
                    ubication = articleUbication,
                    description = articleDescription,
                    count = articleCount,
                    state = articleState
                )
            )
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultTextField(
            value = articleName,
            onTextChange = { articleName = it },
            label = articleName.ifBlank { "Nombre del Artículo" },
            rounded = true
        )

        DefaultDropDownSelector(
            optionList = Category.entries.map { it.displayName },
            labelText = articleCategory.ifBlank { "Categoria" },
            onOptionSelected = { articleCategory = it }
        )

        DefaultDropDownSelector(
            optionList = ubicationList,
            labelText = articleUbication.ifBlank { "Ubicación" },
            onOptionSelected = { articleUbication = it }
        )

        DefaultDropDownSelector(
            optionList = listOf("Nuevo", "Usado", "En Reparación", "Obsoleto"),
            labelText = articleState.ifBlank { "Estado" },
            onOptionSelected = { articleState = it }
        )

        DefaultTextField(
            value = articleCount.toString(),
            onTextChange = {
                try {
                    articleCount = it.toInt()
                } catch (e: NumberFormatException) {
                    context.toast("Número inválido")
                }
            },
            label = "Cantidad",
            singleLine = true,
            rounded = true,
            keyboardType = KeyboardType.Number
        )

        DefaultTextField(
            value = articleDescription,
            onTextChange = { articleDescription = it },
            label = articleDescription.ifBlank { "Descripción (Opcional)" },
            singleLine = false,
            rounded = true,
            minLines = 3
        )
    }
}


