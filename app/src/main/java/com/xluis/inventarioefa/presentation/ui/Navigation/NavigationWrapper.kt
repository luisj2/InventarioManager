package com.xluis.inventarioefa.presentation.ui.Navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.xluis.inventarioefa.presentation.ui.screens.ArticleInfoScreen
import com.xluis.inventarioefa.presentation.ui.screens.Auth.MainAuthScreen
import com.xluis.inventarioefa.presentation.ui.screens.InventoryDataScreen
import com.xluis.inventarioefa.presentation.ui.screens.forms.ArticleMovementsRegisterScreen
import com.xluis.inventarioefa.presentation.ui.screens.forms.CreateArticleScreen
import com.xluis.inventarioefa.presentation.ui.screens.forms.EditArticleScreen
import com.xluis.inventarioefa.presentation.ui.screens.forms.TakeItemScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    val navigateBack: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = MainAuth) {
        composable<MainAuth> {
            MainAuthScreen(
                navigateToInventaryData = {
                    navController.navigate(InventoryData) {
                        popUpTo(MainAuth) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<InventoryData> {
            InventoryDataScreen(
                navigateToArticleInfo = { articleId -> navController.navigate(ArticleInfo(articleId)) },
                navigateToCreateArticle = { navController.navigate(CreateArticle) },
                navigateToMovementRegister = { navController.navigate(ArticleMovementsRegister) },
                returnToAuth = {
                    navController.navigate(MainAuth) {
                        launchSingleTop = true

                    }
                },
                navigateToEditArticle = {articleId -> navController.navigate(EditArticle(articleId))}
            )
        }
        composable<ArticleInfo> { backStackEntry ->
            val articleInfo: ArticleInfo = backStackEntry.toRoute()
            val articleId = articleInfo.articleId
            ArticleInfoScreen(
                articleId = articleId,
                navigateBack = navigateBack,
                navigateTakeItem = { navController.navigate(TakeItem(articleId)) },
                navigateArticleMovementRegister = { navController.navigate(ArticleMovementsRegister) }
            )
        }
        composable<CreateArticle> {
            CreateArticleScreen(
                navigateBack = navigateBack
            )
        }


        composable<TakeItem> { backStackEntry ->
            val takeItem: TakeItem = backStackEntry.toRoute()
            TakeItemScreen(
                navigateBack = navigateBack,
                articleId = takeItem.articleId
            )
        }



        composable<ArticleMovementsRegister> {
            ArticleMovementsRegisterScreen(
                navigateBack = navigateBack
            )
        }

        composable<EditArticle> {  backStackEntry->
            val editArticle : EditArticle = backStackEntry.toRoute()
            EditArticleScreen(
                articleId = editArticle.articleId,
                navigateBack = navigateBack
            )
        }
    }
}