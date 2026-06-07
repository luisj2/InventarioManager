package com.xluis.inventarioefa.utils


//FIRESTORE Collections


const val FIRESTORE_UBICATION_COLLECTION = "ubications"

const val FIRESTORE_USER_COLLECTION = "user"


const val FIRESTORE_ZONES_COLLECTION = "zones"
const val FIRESTORE_ZONES_ARTICLE_SUBCOLLECTION = "articles"
const val FIRESTORE_ZONES_MOVEMENTS_SUBCOLLECTION = "articlesMovements"


//FIRESTORE ArticleFirestore Fields
const val FIRESTORE_ARTICLE_NAME_FIRESTORE = "name"
const val FIRESTORE_ARTICLE_COUNT = "count"
const val FIRESTORE_ARTICLE_ZONEID = "zoneId"







//Firestore UserFirestore Fields
const val FIRESTORE_USER_TAKED_ARTICLE_COUNT_FIELD_FIRESTORE = "articlesTakedCount"
const val FIRESTORE_USER_ZONES_LIST_FIELD = "zonesIdList"
const val FIRESTORE_USER_MOVEMENTS_LIST_FIELD = "movementsIdList"
const val FIRESTORE_USER_USERNAME_FIELD = "userName"
const val FIRESTORE_USER_REQUESTS_FIELD = "requests"
const val FIRESTORE_USER_EMAIL_FIELD = "email"
//Firestore com.xluis.inventarioefa.data.Model.Zone.ZoneFirestore Fields
const val FIRESTORE_ZONE_CHILD_LIST_FIELD_FIRESTORE = "childIdList"
const val FIRESTORE_ZONE_MEMEBERS_FIELD = "membersId"
const val FIRESTORE_ZONE_OWNER_FIELD = "ownerId"
const val FIRESTORE_ZONE_PARENT_LIST_FIELD = "parentIdList"
const val FIRESTORE_ZONE_NAME_FIELD = "name"
const val FIRESTORE_ZONE_MEMBERS_FIELD = "members"


//Firestore Movements Fields
const val FIRESTORE_MOVEMENTS_ZONEID_FIELD = "zoneId"
const val FIRESTORE_MOVEMENTS_USERID_FIELD = "userId"

//Firestore Article Fields
const val FIRESTORE_ARTICLE_COUNT_FIELD = "count"
const val FIRESTORE_ARTICLE_DESCRIPTION_FIELD = "descriptions"

//Firestore Request Fields
const val FIRESTORE_ZONEREQUEST_ZONEID_FIELD = "zoneId"

//DATASTORE
//USER DATASTORE
const val USER_PREFS_KEY = "user_prefs"
const val USER_DATASTORE_UID = "user_uid"

//OTHER
const val SVG_CODE = "image/svg+xml"
const val YOUR_MOVE_ROOM = "Tu"
const val SELECTED_ARTICLES_KEY = "selected_articles"
const val SELECTED_MOVEMENTS_KEY = "selected_movements"

//LOCAL DATABASE
const val DATABASE_NAME = "Inventary_DB"
