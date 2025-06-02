package com.xluis.inventarioefa.utils


//FIRESTORE Collections
const val FIRESTORE_ARTICLE_COLLECTION = "articles"
const val FIRESTORE_ARTICLE_TAKED_ARTICLE_SUBCOLLECTION = "takedArticles"
const val FIRESTORE_RETURN_ARTICLE_SUBCOLLECTION = "returnArticles"

const val FIRESTORE_UBICATION_COLLECTION = "ubications"

const val FIRESTORE_USER_COLLECTION = "user"
const val FIRESTORE_USER_TAKED_ARTICLE_SUBCOLLECTION = "takedArticles"
const val FIRESTORE_USER_RETURN_ARTICLE_SUBCOLLECTION = "returnArticles"


//FIRESTORE Article Fields
const val FIRESTORE_ARTICLE_NAME = "name"
const val FIRESTORE_ARTICLE_COUNT = "count"

//ArticleTaked Fields
const val FIRESTORE_ARTICLE_ARTICLE_TAKED_BASE_ID_FIELD = "baseId"
const val FIRESTORE_ARTICLE_ARTICLE_TAKED_COUNT_FIELD = "articlesTakedCount"
const val FIRESTORE_ARTICLE_ARTICLE_RETURN_COUNT_FIELD = "articlesReturnCount"

//ArticleReturn Fields
const val FIRESTORE_ARTICLE_ARTICLE_RETURN_BASE_ID_FIELD = "baseId"


//Firestore Ubication Fields
const val FIRESTORE_UBICATION_NAME_FIELD = "name"


//Firestore User Fields
const val FIRESTORE_USER_TAKED_ARTICLE_COUNT_FIELD = "articlesTakedCount"