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
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.GetUserIdByEmail
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.AcceptZoneRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.DeleteUserZoneRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.GetUserLoggedUsername
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.User.UserZoneRequest.InsertZoneUserRequest
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.CreateZoneFirestoreCase
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreUserZones
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneData
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetFirestoreZoneNameById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetUserZonesIds
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.GetZoneListByIdList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.RemoveZoneMember
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.SaveFirestoreZoneChanges
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.SaveMovementListInZone
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.GetFirestoreZoneMovementListById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.GetMovementsByZoneIdList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticleMovements.InsertMovementsInZone
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.GetFirestoreArticleListByZoneId
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.GetZoneArticleById
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.MoveArticleToZone
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.RemoveFirestoreArticleList
import com.xluis.inventarioefa._domain.UseCases.Firebase.Firestore.Zone.ZoneArticles.SaveArticleListInZone
import com.xluis.inventarioefa._domain.UseCases.GetUserZonesSummary
import com.xluis.inventarioefa._domain.UseCases.RemoveZoneListCase
import com.xluis.inventarioefa._domain.UseCases.Room.Article.CreateArticleCase
import com.xluis.inventarioefa._domain.UseCases.Room.Article.GetAllArticles
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
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthLoginRepository
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthRegisterRepository
import com.xluis.inventarioefa.data.Database.Firebase.Auth.AuthSessionRepository
import com.xluis.inventarioefa.data.Database.Firebase.Firestore.User.UserZonesRequestsRepository
import com.xluis.inventarioefa.data.Database.Firestore.User.UserFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.User.UserZonesRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleMovementRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ArticleZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Firestore.Zone.ZoneFirestoreRepository
import com.xluis.inventarioefa.data.Database.Room.Articles.ArticleRepository
import com.xluis.inventarioefa.data.Database.Room.InventaryDatabase
import com.xluis.inventarioefa.data.Database.Room.InventoryDatabaseBuilder
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
    val authRegisterRepository by lazy { AuthRegisterRepository(auth) }
    val userFirestoreRepository by lazy { UserFirestoreRepository(fs) }
    val loginRepository by lazy { AuthLoginRepository(auth) }
    val userZonesRepository by lazy { UserZonesRepository(fs) }
    val zoneFirestoreRepository by lazy { ZoneFirestoreRepository(fs) }
    val articleZoneRepository by lazy { ArticleZoneFirestoreRepository(fs) }
    val movementsRepository by lazy { ArticleMovementRepository(fs) }
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


    // UseCases
    val registerAndSaveUserUseCase by lazy {
        RegisterAndSaveUserUseCase(authRegisterRepository, userFirestoreRepository)
    }
    val loginUseCase by lazy { LoginUserUseCase(loginRepository) }
    val getUserZonesIds by lazy { GetUserZonesIds(userZonesRepository) }
    val getZoneListByIdList by lazy { GetZoneListByIdList(zoneFirestoreRepository) }
    val createZoneFirestoreCase by lazy {
        CreateZoneFirestoreCase(
            userZonesRepository = userZonesRepository,
            zonesFirestoreRepository = zoneFirestoreRepository,
            articleRepository = articleZoneRepository,
            movementRepository = movementsRepository
        )
    }
    val getFirestoreZoneData by lazy {
        GetFirestoreZoneData(
            zoneRepository = ZoneFirestoreRepository(fs),
            articleZoneRepository = ArticleZoneFirestoreRepository(fs),
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
        GetZoneArticleById(articleZoneRepository)
    }

    val moveArticleToZone by lazy {
        MoveArticleToZone(
            articleFirestoreRepository = articleZoneRepository,
            movementFirestoreRepository = movementsRepository,
            articleRoomRepository = articleRoomRepository,
            movementRoomRepository = movementRoomRepository
        )
    }

    val getMovementsByZoneIdList by lazy {
        GetMovementsByZoneIdList(movementsRepository)
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
        GetUserZonesSummary(userZonesRepository, zoneFirestoreRepository, zoneRoomRepository)
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
    val insertZoneUserRequest by lazy {
        InsertZoneUserRequest(userZonesRequestRequest)
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

    val getFirestoreUserZones by lazy {
        GetFirestoreUserZones(zoneFirestoreRepository, articleZoneRepository)
    }
    val getUserIdByEmail by lazy{
        GetUserIdByEmail(userFirestoreRepository)
    }

    val getAllRoomMovements by lazy{
        GetAllMovementsUserZones(zoneRoomRepository)
    }

    val insertArticleListRoom by lazy{
        InsertArticleListRoom(articleRoomRepository)
    }

    val insertMovementsListRoom by lazy{
        InsertMovementsListRoom(movementRoomRepository)
    }

    val getZoneArticlesByZoneId by lazy{
        GetZoneArticlesByZoneId(zoneRoomRepository)
    }

    val getFirestoreArticleListByZoneId by lazy{
        GetFirestoreArticleListByZoneId(articleZoneRepository)
    }
    val deleteRoomArticleList by lazy {
        DeleteRoomArticleList(articleRoomRepository)
    }

    val deleteFirestoreArticleListByZoneId by lazy{
        RemoveFirestoreArticleList(articleZoneRepository)
    }
    val getRoomZoneNameById by lazy{
        GetRoomZoneNameById(zoneRoomRepository)
    }
    val saveRoomChanges by lazy{
        SaveRoomChanges(articleRoomRepository, movementRoomRepository)
    }

    val saveFirestoreZoneChanges by lazy {
        SaveFirestoreZoneChanges(articleZoneRepository, movementsRepository)
    }
    val getFirestoreZoneMovementListById by lazy {
        GetFirestoreZoneMovementListById(movementsRepository)
    }

    val getRoomZoneMovementById by lazy{
        GetRoomZoneMovementById(zoneRoomRepository)
    }

    val insertMovementsInZone by lazy{
        InsertMovementsInZone(movementsRepository)
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
        registerViewModel(LoginViewModel::class) { LoginViewModel(loginUseCase) }
        registerViewModel(ZonePrincipalViewModel::class) {
            ZonePrincipalViewModel(
                getFirestoreUserZones = getFirestoreUserZones,
                getRoomZoneList = getRoomZoneList,
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
                getFirestoreZoneData = getFirestoreZoneData,
                getRoomZone = getRoomZone,
                getZoneArticlesByZoneId = getZoneArticlesByZoneId,
                getFirestoreArticleListByZoneId = getFirestoreArticleListByZoneId,
                insertZoneUserRequest = insertZoneUserRequest,
                removeZoneMember = removeZoneMember,
                getUserByEmail = getUserIdByEmail,
                getUserLoggedEmail = getUserLoggedEmail,
                getFirestoreZoneNameById = getFirestoreZoneNameById,
                getRoomZoneNameById = getRoomZoneNameById,
                getFirestoreUsername = getUserLoggedUsername,
                saveRoomChanges = saveRoomChanges,
                saveFirestoreZoneChanges = saveFirestoreZoneChanges,
                getRoomZoneMovementById = getRoomZoneMovementById,
                getFirestoreZoneMovementListById = getFirestoreZoneMovementListById,
                removeFirestoreArticleList = deleteFirestoreArticleListByZoneId,
                deleteRoomArticleList = deleteRoomArticleList,
                insertFirestoreMovements = insertMovementsInZone,
                insertRoomMovements = insertMovementsListRoom
            )

        }

        registerViewModel(ArticleListSelectorViewModel::class) {
            ArticleListSelectorViewModel(
                getRoomArticles = getRoomArticles,
                getFirestoreZoneNameById = getFirestoreZoneNameById,
                getRoomZoneNameById = getRoomZoneNameById,
                createArticle = createArticleCase
            )
        }
        registerViewModel(ZoneSelectorViewModel::class) {
            ZoneSelectorViewModel(
                getUserZonesIds = getUserZonesIds,
                getZoneListByIdList = getZoneListByIdList,
                getZoneArticleById = getArticlesById,
                moveArticleToZone = moveArticleToZone,
                getRoomZoneList = getRoomZoneList,
                hasInternet = appContext.hasConexion()
            )
        }

        registerViewModel(MainScreenViewModel::class) {
            MainScreenViewModel()
        }

        registerViewModel(YourMovementsViewModel::class) {
            YourMovementsViewModel(
                getUserZonesIds = getUserZonesIds,
                getMovementsByZoneIdList = getMovementsByZoneIdList,
                getAllRoomMovements = getAllRoomMovements
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
                acceptZoneRequest = acceptZoneRequest
            )
        }


    }
}
