package com.xluis.inventarioefa.presentation.ui.screens.forms

import android.net.Uri
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
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication.UbicationFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Ubication.UbicationFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDropDownSelector
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultTextField
import com.xluis.inventarioefa.utils.DefaultTopBar
import com.xluis.inventarioefa.utils.cleanFields
import com.xluis.inventarioefa.utils.handleErrors
import com.xluis.inventarioefa.utils.toast

@Composable
fun CreateArticleScreen(
    navigateBack: () -> Unit
) {
    val articleViewModel: ArticleFirestoreViewModel = viewModel(
        factory = ArticleFirestoreViewModelBuilder.getArticleViewModelFactory()
    )

    val ubicationViewModel: UbicationFirestoreViewModel = viewModel(
        factory = UbicationFirestoreViewModelBuilder.getUbicationFirestoreViewModelFactory()
    )

    val ubicationListResult by ubicationViewModel.allUbicationsList.collectAsState()
    val inserArticleResult by articleViewModel.insertArticleStatus.collectAsState()

    val context = LocalContext.current

    LaunchedEffect(ubicationListResult) {
        arrayOf(ubicationListResult).handleErrors { message -> context.toast(message) }
    }
    LaunchedEffect(inserArticleResult) {
        if (inserArticleResult is SuspendResult.Success && (inserArticleResult as SuspendResult.Success<Boolean>).data) {
            context.toast("Artículo insertado correctamente")
        }
    }


    val ubicationList = if (ubicationListResult is SuspendResult.Success) {
        (ubicationListResult as SuspendResult.Success).data
    } else {
        emptyList()
    }

    val isLoading = (ubicationListResult is SuspendResult.Loading)


    LaunchedEffect(Unit) {
        ubicationViewModel.getAllUbications()
    }

    ViewScreenContent(
        ubicationList = ubicationList,
        navigateBack = navigateBack,
        onInsertArticle = { articleToInsert ->
            articleViewModel.insertArticleInFirestore(
                articleToInsert
            )
        }
    )
    if (isLoading) DefaultLoadingScreen()
}


@Composable
private fun ViewScreenContent(
    ubicationList: List<Ubication>,
    navigateBack: () -> Unit,
    onInsertArticle: (Article) -> Unit
) {
    var cleanFieldsCall by remember { mutableStateOf(false) }
    var article by remember { mutableStateOf<Article?>(null) }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {


        DefaultTopBar(
            title = "Crear Artículo",
            backButtonLogic = navigateBack
        )

        ArticleFields(
            modifier = Modifier.fillMaxWidth(),
            onFiledsArticle = { fieldsArticle -> article = fieldsArticle },
            cleanFieldsCall = cleanFieldsCall,
            ubicationList.map { it.name }
        )



        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp),
            contentText = "Guardar",
            onClick = {
                if (!isValidArticle(article)) context.toast("Completa todos los campos obligatorios")
                else {
                    onInsertArticle(article!!)
                    cleanFieldsCall = true
                }
            }
        )

    }
}

@Composable
private fun ArticleFields(
    modifier: Modifier = Modifier,
    onFiledsArticle: (Article) -> Unit,
    cleanFieldsCall: Boolean,
    ubicationList: List<String>
) {


    val articleName = remember { mutableStateOf("") }
    val articleCategory = remember { mutableStateOf("") }
    val articleUbication = remember { mutableStateOf("") }
    val articleDescription = remember { mutableStateOf("") }
    val articleCount = remember { mutableIntStateOf(1) }
    val articleState = remember { mutableStateOf("") }
    val articleImageUri = remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    onFiledsArticle(
        Article(
            name = articleName.value,
            category = articleCategory.value,
            ubication = articleUbication.value,
            description = articleDescription.value,
            count = articleCount.intValue,
            state = articleState.value
        )
    )

    if (cleanFieldsCall)
        listOf(
            articleName,
            articleCategory,
            articleUbication,
            articleDescription,
            articleCount,
            articleState,
            articleImageUri
        ).cleanFields()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(9.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DefaultTextField(
            value = articleName.value,
            onTextChange = { textFieldTxt -> articleName.value = textFieldTxt },
            label = "Nombre del Artículo",
            rounded = true
        )

        DefaultDropDownSelector(
            optionList = Category.entries.map { it.displayName },
            labelText = "Categoría del Artículo",
            onOptionSelected = { categorySelected -> articleCategory.value = categorySelected }
        )

        DefaultDropDownSelector(
            optionList = ubicationList,
            labelText = "Ubicación",
            onOptionSelected = { ubicationSelected -> articleUbication.value = ubicationSelected }
        )

        DefaultDropDownSelector(
            optionList = listOf(
                "Nuevo",
                "Usado",
                "En Reparación",
                "Obsoleto"
            ),
            labelText = "Estado",
            onOptionSelected = { state -> articleState.value = state }
        )

        DefaultTextField(
            value = articleCount.intValue.toString(),
            onTextChange = { newText ->
                if (newText.isEmpty() || newText.all { it.isDigit() }) {
                    try {
                        articleCount.intValue = newText.toInt()
                    } catch (e: NumberFormatException) {
                        context.toast("Caracter invalido")
                    }
                }
            },
            label = "Cantidad",
            singleLine = true,
            rounded = true,
            maxLines = 1,
            keyboardType = KeyboardType.Number
        )
        DefaultTextField(
            value = articleDescription.value,
            onTextChange = { textFieldTxt -> articleDescription.value = textFieldTxt },
            label = "Descripción (Opcional)",
            singleLine = false,
            rounded = true,
            minLines = 3
        )
    }


}


private fun isValidArticle(article: Article?): Boolean {
    if (article == null) return false
    return listOf(article.name, article.category, article.ubication).all { !it.isNullOrEmpty() }
}




