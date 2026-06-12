package com.xluis.inventarioefa.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.GetUserLoggedEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.IsUserLoggedIn
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.LoginUserUseCase
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.Logout
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.RegisterAndSaveUserUseCase
import com.xluis.inventarioefa._domain.UseCases.Firebase.Auth.SendPasswordResetEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.GetUserIdByEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.GetUserNameById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.AcceptZoneRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.DeleteUserZoneRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserLoggedUsername
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserRequests
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.SendZoneUserRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.CreateZoneFirestoreCase
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetAllZoneListByUserId
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneData
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneNameById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetMembersFlow
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetUserZonesIds
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetZoneListByIdList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.RemoveZoneMember
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.SaveFirestoreZoneChanges
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.SaveMovementListInZone
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.GetFirestoreZoneMovementListById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.GetMovementsByZoneIdList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.InsertMovementsInZone
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.GetFirestoreArticleListByZoneId
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.RemoveFirestoreArticleList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.SaveArticleListInZone
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.ChangeZoneName
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.DeleteArticleDescriptionList
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetAllUserMovements
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetAllZoneList
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetArticlesByZoneId
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetMovementsByZoneId
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetUserZonesSummary
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetZoneArticleById
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetZoneById
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.GetZoneNameById
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.InsertMovements
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.MoveArticleToZone
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.RemoveArticlesByIdList
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.RemoveZoneListCase
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.SaveDatabaseChanges
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.UpdateArticleCount
import com.xluis.inventarioefa._domain.UseCases.FirebaseAndRoom.UpdateDescription
import com.xluis.inventarioefa._domain.UseCases.Room.AddArtilesAndMovementSelected
import com.xluis.inventarioefa._domain.UseCases.Room.Article.CreateArticleCase
import com.xluis.inventarioefa._domain.UseCases.Room.Article.GetAllArticles
import com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected.ClearAllArticleAndMovementSelected
import com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected.GetArticlesByScreenAndZoneIds
import com.xluis.inventarioefa._domain.UseCases.Room.ArticleSelected.RemoveArticleSelectedListByIds
import com.xluis.inventarioefa._domain.UseCases.Room.MovementSelected.GetMovementsByScreenAndZoneIds
import com.xluis.inventarioefa._domain.UseCases.Room.RemoveArticlesAndMovementByArticleId
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article.DeleteRoomArticleList
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article.GetZoneArticlesByZoneId
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Article.InsertArticleListRoom
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.CreateZoneWithParentRoomCase
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZone
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZoneList
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.GetRoomZoneNameById
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements.GetAllMovementsUserZones
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements.GetRoomZoneMovementById
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.Movements.InsertMovementsListRoom
import com.xluis.inventarioefa._domain.UseCases.Room.Zone.SaveRoomChanges
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthRepository
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthSessionRepository
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.User.UserZonesRequestsRepository
import com.xluis.inventarioefa.data.Database.Firestore.User.UserFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Articles.ArticleRepository
import com.xluis.inventarioefa.data.Database.Room.ArticlesSelected.ArticleSelectedRepository
import com.xluis.inventarioefa.data.Database.Room.InventaryDatabase
import com.xluis.inventarioefa.data.Database.Room.InventoryDatabaseBuilder
import com.xluis.inventarioefa.data.Database.Room.MovementSelected.MovementSelectedRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.Article_Movements.ArticleZoneMovementRoomRepository
import com.xluis.inventarioefa.data.Database.Room.Zone.ZoneRoomRepository
import com.xluis.inventarioefa.domain.model.Database.Room.Article.ArticleZoneRoomRepository
import com.xluis.inventarioefa.presentation.ViewModel.GenericViewModelFactory
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Login.LoginViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Auth.Register.RegisterViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.MainScreen.MainScreenViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.Settings.SettingsViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.YourMovements.YourMovementsViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZonePrincipal.ZonePrincipalViewModel
import com.xluis.inventarioefa.presentation.ui.screens.PrincipalScreens.ZoneRequestsScreen.ZoneRequestsViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Selectors.ArticleListSelector.ArticleListSelectorViewModel
import com.xluis.inventarioefa.presentation.ui.screens.Selectors.ZoneSelector.ZoneSelectorViewModel
import com.xluis.inventarioefa.presentation.ui.screens.ZoneInfo.ZoneInfoViewModel
import com.xluis.inventarioefa.presentation.ui.screens.forms.CreateZone.CreateZoneViewModel
import com.xluis.inventarioefa.utils.hasConexion
import kotlin.reflect.KClass

object AppDependencies {

    private lateinit var appContext: Context
    fun init(context: Context) {
        appContext = context
    }

    // Firebase
    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val fs: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    val db: InventaryDatabase by lazy { InventoryDatabaseBuilder.getDatabase(appContext) }

    // Repositorios
    val userFirestoreRepository by lazy { UserFirestoreRepository(fs) }
    val authRepository by lazy{ AuthRepository(auth)}
    val userZonesRepository by lazy { UserZonesRepository(fs) }
    val zoneFirestoreRepository by lazy { ZoneFirestoreRepository(fs) }
    val articleZoneRepository by lazy { ArticleZoneFirestoreRepository(fs) }
    val movementsFirestoreRepository by lazy { ArticleMovementRepository(fs) }
    val sessionRepository by lazy { AuthSessionRepository(auth) }
    val zoneRoomRepository by lazy {
        ZoneRoomRepository(
            zoneDao = db.zoneDao(),
            movementDao = db.articleMovementDao(),
            articleZoneDao = db.articleZoneDao(),
            db = db
        )
    }
    val articleRepository by lazy { ArticleRepository(db.articleDao()) }
    val articleRoomRepository by lazy { ArticleZoneRoomRepository(db.articleZoneDao()) }
    val movementRoomRepository by lazy { ArticleZoneMovementRoomRepository(db.articleMovementDao()) }
    val userZonesRequestRequest by lazy { UserZonesRequestsRepository(fs) }
    val articleFirestoreRepository by lazy { ArticleZoneFirestoreRepository(fs) }
    val articleSelectedRepository by lazy{ ArticleSelectedRepository(db.articleSelectedDao()) }
    val movementsselectedRepository by  lazy{ MovementSelectedRepository(db.movementSelectedDao()) }


    // UseCases
    val registerAndSaveUserUseCase by lazy {
        RegisterAndSaveUserUseCase(authRepository, userFirestoreRepository)
    }
    val loginUseCase by lazy { LoginUserUseCase(authRepository) }
    val getUserZonesIds by lazy { GetUserZonesIds(userZonesRepository) }
    val getZoneListByIdList by lazy { GetZoneListByIdList(zoneFirestoreRepository) }
    val createZoneFirestoreCase by lazy {
        CreateZoneFirestoreCase(
            zonesFirestoreRepository = zoneFirestoreRepository
        )
    }
    val getFirestoreZoneData by lazy {
        GetFirestoreZoneData(
            zoneRepository = ZoneFirestoreRepository(fs),
            articleZoneRepository = articleFirestoreRepository,
            movementsZoneRepository = ArticleMovementRepository(fs)
        )
    }
    val saveMovementListInZone by lazy {
        SaveMovementListInZone(
            movementRepository = ArticleMovementRepository(fs)
        )
    }
    val saveArticleListInZone by lazy {
        SaveArticleListInZone(
            articleZoneRepository
        )
    }

    val getRoomArticles by lazy {
        GetAllArticles(
            ArticleRepository(
                dao = db.articleDao()
            )
        )
    }
    val getRoomZoneList by lazy {
        GetRoomZoneList(zoneRoomRepository)
    }

    val getArticlesById by lazy {
        GetZoneArticleById(articleZoneRepository, articleRoomRepository)
    }

    val getAllZoneListByUserId by lazy {
        GetAllZoneListByUserId(
            zoneFirestoreRepository, zoneRoomRepository
        )
    }

    val moveArticleToZone by lazy {
        MoveArticleToZone(
            zoneFirestoreRepository = zoneFirestoreRepository,
            zoneRoomRepository = zoneRoomRepository
        )
    }

    val getMovementsByZoneIdList by lazy {
        GetMovementsByZoneIdList(movementsFirestoreRepository)
    }

    val getUserLoggedEmail by lazy {
        GetUserLoggedEmail(sessionRepository)
    }

    val getRoomZone by lazy {
        GetRoomZone(zoneRoomRepository)
    }
    val isUserLoggedIn by lazy {
        IsUserLoggedIn(sessionRepository)
    }

    val createZoneWithParentRoomCase by lazy {
        CreateZoneWithParentRoomCase(zoneRoomRepository)
    }

    val createArticleCase by lazy {
        CreateArticleCase(articleRepository = articleRepository)
    }

    val removeZoneListCase by lazy {
        RemoveZoneListCase(zoneRoomRepository, zoneFirestoreRepository)
    }

    val getUserZonesSummary by lazy {
        GetUserZonesSummary( zoneFirestoreRepository)
    }

    val getFirestoreZoneNameById by lazy {
        GetFirestoreZoneNameById(zoneFirestoreRepository)
    }

    val logout by lazy {
        Logout(sessionRepository)
    }

    val getUserLoggedUsername by lazy {
        GetUserLoggedUsername(userZonesRepository)
    }

    // UseCases para ZoneInfoViewModel
    val sendZoneUserRequest by lazy {
        SendZoneUserRequest(userZonesRequestRequest)
    }

    val removeZoneMember by lazy {
        RemoveZoneMember(zoneFirestoreRepository)
    }

    // UseCases para ZoneRequestsViewModel
    val deleteUserZoneRequest by lazy {
        DeleteUserZoneRequest(userZonesRequestRequest)
    }

    val acceptZoneRequest by lazy {
        AcceptZoneRequest(zoneFirestoreRepository)
    }


    val getUserIdByEmail by lazy {
        GetUserIdByEmail(userFirestoreRepository)
    }

    val getAllRoomMovements by lazy {
        GetAllMovementsUserZones(zoneRoomRepository)
    }

    val insertArticleListRoom by lazy {
        InsertArticleListRoom(articleRoomRepository)
    }

    val insertMovementsListRoom by lazy {
        InsertMovementsListRoom(movementRoomRepository)
    }

    val getZoneArticlesByZoneId by lazy {
        GetZoneArticlesByZoneId(zoneRoomRepository)
    }

    val getFirestoreArticleListByZoneId by lazy {
        GetFirestoreArticleListByZoneId(articleZoneRepository)
    }
    val deleteRoomArticleList by lazy {
        DeleteRoomArticleList(articleRoomRepository)
    }

    val deleteFirestoreArticleListByZoneId by lazy {
        RemoveFirestoreArticleList(articleZoneRepository)
    }
    val getRoomZoneNameById by lazy {
        GetRoomZoneNameById(zoneRoomRepository)
    }
    val saveRoomChanges by lazy {
        SaveRoomChanges(zoneRoomRepository)
    }

    val saveFirestoreZoneChanges by lazy {
        SaveFirestoreZoneChanges(articleZoneRepository, movementsFirestoreRepository)
    }
    val getFirestoreZoneMovementListById by lazy {
        GetFirestoreZoneMovementListById(movementsFirestoreRepository)
    }

    val getRoomZoneMovementById by lazy {
        GetRoomZoneMovementById(zoneRoomRepository)
    }

    val insertMovementsInZone by lazy {
        InsertMovementsInZone(movementsFirestoreRepository)
    }

    val getUserRequest by lazy {
        GetUserRequests(userZonesRequestRequest)
    }

    val getZoneById by lazy {
        GetZoneById(zoneRoomRepository, zoneFirestoreRepository)
    }

    val getArticlesByZoneId by lazy {
        GetArticlesByZoneId(zoneRoomRepository, articleZoneRepository)
    }

    val getZoneNameById by lazy {
        GetZoneNameById(zoneFirestoreRepository, zoneRoomRepository)
    }

    val saveDatabaseChanges by lazy {
        SaveDatabaseChanges(zoneRoomRepository, zoneFirestoreRepository)
    }

    val getMovementsByZoneId by lazy {
        GetMovementsByZoneId(zoneRoomRepository, movementsFirestoreRepository)
    }

    val removeArticlesByIdList by lazy {
        RemoveArticlesByIdList(articleRoomRepository, articleFirestoreRepository)
    }

    val insertMovements by lazy {
        InsertMovements(movementRoomRepository, movementsFirestoreRepository)
    }

    val getAllUserMovements by lazy {
        GetAllUserMovements(zoneFirestoreRepository, zoneRoomRepository)
    }

    val getUserNameById by lazy {
        GetUserNameById(userFirestoreRepository)
    }
    val updateArticleCount by lazy{
        UpdateArticleCount(zoneRoomRepository, zoneFirestoreRepository)
    }

    val getArticlesByScreenAndZoneIds by lazy{
        GetArticlesByScreenAndZoneIds(articleSelectedRepository)
    }
    val getMovementsByScreenAndZoneIds by lazy{
        GetMovementsByScreenAndZoneIds(movementsselectedRepository)
    }

    val addArticlesAndMovementsSelected by lazy{
        AddArtilesAndMovementSelected(articleSelectedRepository, movementsselectedRepository)
    }

    val removeArticleSelectedListByIds by lazy{
        RemoveArticleSelectedListByIds(articleSelectedRepository)
    }
    val clearAllArticleAndMovementSelected by lazy{
        ClearAllArticleAndMovementSelected(articleSelectedRepository, movementsselectedRepository   )
    }

    val removeArticlesAndMovementByArticleId by lazy{
        RemoveArticlesAndMovementByArticleId(articleSelectedRepository, movementsselectedRepository)
    }

    val getAllZoneList by lazy{
        GetAllZoneList(zoneRoomRepository, zoneFirestoreRepository)
    }

    val sendPasswordResetEmail by lazy{
        SendPasswordResetEmail(authRepository)
    }

    val changeZoneName by lazy{
        ChangeZoneName(zoneFirestoreRepository, zoneRoomRepository)
    }
    val updateDescription by lazy{
        UpdateDescription(zoneRoomRepository,zoneFirestoreRepository)
    }
    val deleteArticleDescriptionList by lazy{
        DeleteArticleDescriptionList(articleRoomRepository, articleFirestoreRepository)
    }

    val getMembersFlow by lazy{
        GetMembersFlow(zoneFirestoreRepository)
    }

    // Mapa de factories
    private val factories = mutableMapOf<KClass<out ViewModel>, ViewModelProvider.Factory>()

    // Registrar un ViewModel genérico
    private fun <T : ViewModel> registerViewModel(kClass: KClass<T>, creator: () -> T) {
        factories[kClass] = GenericViewModelFactory(creator)
    }

    // Obtener factory por tipo
    @Suppress("UNCHECKED_CAST")
    fun <T : ViewModel> getFactory(kClass: KClass<T>): ViewModelProvider.Factory {
        return factories[kClass]
            ?: throw IllegalArgumentException("No factory registered for ${kClass.simpleName}")
    }

    // Inicializar todas las factories de la app
    fun setupFactories() {
        registerViewModel(RegisterViewModel::class) { RegisterViewModel(registerAndSaveUserUseCase) }
        registerViewModel(LoginViewModel::class){
            LoginViewModel(
                loginUserUseCase = loginUseCase,
                sendPasswordResetEmail = sendPasswordResetEmail
            )
        }
        registerViewModel(ZonePrincipalViewModel::class) {
            ZonePrincipalViewModel(
                getAllZoneList = getAllZoneList,
                isUserLoggedIn = isUserLoggedIn,
                removeZoneListCase = removeZoneListCase
            )

        }
        registerViewModel(CreateZoneViewModel::class) {
            CreateZoneViewModel(
                createZoneCase = createZoneFirestoreCase,
                createZoneWithParentRoomCase = createZoneWithParentRoomCase,
                createArticleCase = createArticleCase,
                getUserZonesSummary = getUserZonesSummary,
                getUserLoggedEmail = getUserLoggedEmail,
                getRoomZone = getRoomZone,
                getFirestoreZoneNameById = getFirestoreZoneNameById,
                getRoomZoneList = getRoomZoneList
            )
        }

        registerViewModel(ZoneInfoViewModel::class) {
            ZoneInfoViewModel(
                getZoneById = getZoneById,
                getArticlesByZoneId = getArticlesByZoneId,
                sendZoneUserRequest = sendZoneUserRequest,
                removeZoneMember = removeZoneMember,
                getUserIdByEmail = getUserIdByEmail,
                getUserLoggedEmail = getUserLoggedEmail,
                getZoneNameById = getZoneNameById,
                getFirestoreUsername = getUserLoggedUsername,
                saveDatabaseChanges = saveDatabaseChanges,
                getMovementsByZoneId = getMovementsByZoneId,
                removeArticlesByIdList = removeArticlesByIdList,
                insertMovements = insertMovements,
                getUserNameById = getUserNameById,
                updateArticleCount = updateArticleCount,
                getArticlesByScreenAndZoneIds = getArticlesByScreenAndZoneIds,
                getMovementsByScreenAndZoneIds = getMovementsByScreenAndZoneIds,
                removeArticleSelectedListByIds = removeArticleSelectedListByIds,
                clearAllArticleAndMovementSelected = clearAllArticleAndMovementSelected,
                getUserLoggedUsername = getUserLoggedUsername,
                changeZoneName = changeZoneName,
                updateDescription = updateDescription,
                deleteArticleDescriptionList = deleteArticleDescriptionList,
                getMembersFlow = getMembersFlow
            )


        }

        registerViewModel(ArticleListSelectorViewModel::class) {
            ArticleListSelectorViewModel(
                getRoomArticles = getRoomArticles,
                getFirestoreZoneNameById = getFirestoreZoneNameById,
                getRoomZoneNameById = getRoomZoneNameById,
                createArticle = createArticleCase,
                addArtilesAndMovementSelected = addArticlesAndMovementsSelected,
                getUserLoggedUsername = getUserLoggedUsername
            )
        }
        registerViewModel(ZoneSelectorViewModel::class) {
            ZoneSelectorViewModel(
                getAllZoneListByUserId = getAllZoneListByUserId,
                getZoneArticleById = getArticlesById,
                moveArticleToZone = moveArticleToZone,
                hasInternet = appContext.hasConexion(),
                getUserLoggedUsername = getUserLoggedUsername
            )
        }

        registerViewModel(MainScreenViewModel::class) {
            MainScreenViewModel()
        }

        registerViewModel(YourMovementsViewModel::class) {
            YourMovementsViewModel(
                getAllUserMovements = getAllUserMovements
            )
        }

        registerViewModel(SettingsViewModel::class) {
            SettingsViewModel(
                isUserLoggedIn = isUserLoggedIn,
                getUserLoggedUsername = getUserLoggedUsername,
                logout = logout
            )
        }

        registerViewModel(ZoneRequestsViewModel::class) {
            ZoneRequestsViewModel(
                deleteUserZoneRequest = deleteUserZoneRequest,
                acceptZoneRequest = acceptZoneRequest,
                getUserRequests = getUserRequest
            )
        }


    }
}
