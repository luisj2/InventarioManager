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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xluis.inventarioefa.domain.model.DataClass.Article.Article
import com.xluis.inventarioefa.domain.model.DataClass.Article.ArticleMovements.ArticleTaked
import com.xluis.inventarioefa.domain.model.DataClass.Result.SuspendResult
import com.xluis.inventarioefa.domain.model.DataClass.User.User
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Auth.AuthViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article.ArticleFirestoreViewModelBuilder
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User.UserFirestoreViewModel
import com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.User.UserFirestoreViewModelBuilder
import com.xluis.inventarioefa.utils.DefaultButton
import com.xluis.inventarioefa.utils.DefaultDatePicker
import com.xluis.inventarioefa.utils.DefaultInfoField
import com.xluis.inventarioefa.utils.DefaultLoadingScreen
import com.xluis.inventarioefa.utils.DefaultNumeberPickerWithLabel
import com.xluis.inventarioefa.utils.DefaultTextField
import com.xluis.inventarioefa.utils.DefaultTopBar
import com.xluis.inventarioefa.utils.handleErrors
import com.xluis.inventarioefa.utils.toast

@Composable
fun TakeItemScreen(
    navigateBack: () -> Unit,
    articleId: String
) {

    val articleFirestoreViewModel: ArticleFirestoreViewModel =
        viewModel(
            factory = ArticleFirestoreViewModelBuilder.getArticleViewModelFactory()
        )

    val userFirestoreViewModel: UserFirestoreViewModel =
        viewModel(
            factory = UserFirestoreViewModelBuilder.getUserFirestoreViewModelFactory()
        )

    val authViewModel: AuthViewModel =
        viewModel(
            factory = AuthViewModelBuilder.getAuthViewModelFactory()
        )
    val userEmail = authViewModel.getLoggedUserEmail()

    val context = LocalContext.current

    val userArticleTakedInsertResult by userFirestoreViewModel.addArticleTakedStatus.collectAsState()
    val articleArticleTakedInsertResutl by articleFirestoreViewModel.addArticleTakedStatus.collectAsState()
    val subtractArticleResult by articleFirestoreViewModel.substractArticleCountStatus.collectAsState()

    val articleResult by articleFirestoreViewModel.articleById.collectAsState()
    val userNameResult by userFirestoreViewModel.userByEmail.collectAsState()

    LaunchedEffect(Unit) {
        articleFirestoreViewModel.getArticleById(articleId)
        if (userEmail != null) userFirestoreViewModel.getUserByEmail(userEmail)
    }
    LaunchedEffect(
        userArticleTakedInsertResult,
        articleArticleTakedInsertResutl,
        subtractArticleResult,
        articleResult,
        userNameResult
    ) {
        arrayOf(
            userArticleTakedInsertResult,
            articleArticleTakedInsertResutl,
            subtractArticleResult,
            articleResult,
            userNameResult
        ).handleErrors { message ->
            context.toast(message)
        }
    }

    val article = if (articleResult is SuspendResult.Success)
        (articleResult as SuspendResult.Success).data
    else
        null

    val userName = (userNameResult as? SuspendResult.Success<User?>)?.data?.userName ?:  ""

    //Loading
    val userLoading = userArticleTakedInsertResult is SuspendResult.Loading
    val articleLoading =
        articleArticleTakedInsertResutl is SuspendResult.Loading
                || subtractArticleResult is SuspendResult.Loading
    val isLoading = userLoading || articleLoading


    val userArticleTakedInsertStatus =
        userArticleTakedInsertResult is SuspendResult.Success &&
                (userArticleTakedInsertResult as? SuspendResult.Success)?.data == true

    val articleArticleTakedInsertStatus =
        articleArticleTakedInsertResutl is SuspendResult.Success &&
                (articleArticleTakedInsertResutl as? SuspendResult.Success)?.data == true

    val subtractArticleStatus =
        subtractArticleResult is SuspendResult.Success &&
                (subtractArticleResult as? SuspendResult.Success)?.data == true

    val insertStatus =
        userArticleTakedInsertStatus && articleArticleTakedInsertStatus && subtractArticleStatus



    var articleTakedCreated by remember { mutableStateOf<ArticleTaked?>(null) }


    var pressButton by remember { mutableStateOf(false) }

    LaunchedEffect(insertStatus) {
        if(!pressButton) return@LaunchedEffect
        if (insertStatus) {
            context.toast("Se ha sacado correctamente")
            navigateBack()
        } else if(!insertStatus && !isLoading){


            if (userArticleTakedInsertStatus) {
                userFirestoreViewModel.removeArticleTaked(
                    userEmail ?: "", articleTakedCreated?.id ?: ""
                )
            }
            if (articleArticleTakedInsertStatus) {
                articleFirestoreViewModel.removeArticleTakedById(
                    userEmail ?: "", articleTakedCreated?.id ?: ""
                )
            }
            if (subtractArticleStatus) {
                articleFirestoreViewModel.addArticleCountById(
                    articleTakedCreated?.articleId ?: "",
                    articleTakedCreated?.articlesTakedCount ?: 0
                )
            }

            context.toast("Ha ocurrido un error al sacar el artículo")
            pressButton = false
        }
    }






    ScreenViewContent(
        article = article,
        userName = userName,
        navigateBack = navigateBack,
        userEmail = userEmail,
        onRegistArticleTaked = { articleTaked ->

            articleFirestoreViewModel.substractArticleCountById(
                articleId,
                articleTaked.articlesTakedCount
            )

            userFirestoreViewModel.addArticleTaked(
                userId = articleTaked.userId,
                articleTaked = articleTaked
            )

            articleFirestoreViewModel.addArticleTaked(articleId, articleTaked)


            articleTakedCreated = articleTaked
            pressButton = true
        }
    )

    if (isLoading) DefaultLoadingScreen()

}


@Composable
private fun ScreenViewContent(
    article: Article?,
    userName: String,
    navigateBack: () -> Unit,
    userEmail: String?,
    onRegistArticleTaked: (ArticleTaked) -> Unit
) {
    var description by remember { mutableStateOf("") }
    var takeArticleCount by remember { mutableIntStateOf(1) }
    var date by remember { mutableStateOf("") }

    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        DefaultTopBar(
            title = "Sacar Artículo",
            backButtonLogic = navigateBack
        )

        DefaultInfoField(
            label = "Artículo",
            value = article?.name ?: "???"
        )


        DefaultInfoField(
            label = "Usuario",
            value = userName
        )


        DefaultNumeberPickerWithLabel(
            value = takeArticleCount,
            onValueChange = { newValue -> takeArticleCount = newValue },
            label = "Articulos para sacar"
        )


        DefaultDatePicker(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 6.dp),
            onDateSelected = { dateSelected -> date = dateSelected }
        )

        DefaultTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = description,
            onTextChange = { text -> description = text },
            label = "Descripción (Opcional)",
            minLines = 6,
            rounded = true,
            singleLine = false
        )

        DefaultButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentText = "Sacar",
            onClick = {
                if (takeArticleCount > (article?.count ?: -1)) {
                    context.toast("No hay suficientes en el inventario")
                    return@DefaultButton
                }
                if (userEmail == null) {
                    context.toast("No se ha podido añadir el objeto inicia sesion de nuevo")
                    return@DefaultButton
                }

                val articleTaked = ArticleTaked(
                    articleId = article?.id ?: "",
                    userId = userEmail,
                    userName = userName,
                    articleName = article?.name ?: "???",
                    articleCategory = article?.category ?: "???",
                    articlesTakedCount = takeArticleCount,
                    date = date
                )

                onRegistArticleTaked(articleTaked)
            }
        )
    }
}







