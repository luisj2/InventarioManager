package com.xluis.inventarioefa.presentation.ViewModel.Firebase.Firestore.Article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xluis.inventarioefa.domain.model.Database.Firebase.Firestore.Article.ArticleFirestoreRespository


class ArticleFirestoreViewModelFactory(private val repository: ArticleFirestoreRespository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArticleFirestoreViewModel::class.java)) {
            return ArticleFirestoreViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}