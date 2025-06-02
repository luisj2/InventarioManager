package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article

import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Article.ArticleFirestoreRespository

object ArticleFirestoreViewModelBuilder {

    fun getArticleViewModelFactory() =
        ArticleFirestoreViewModelFactory(ArticleFirestoreRespository(FirebaseFirestore.getInstance()))
}