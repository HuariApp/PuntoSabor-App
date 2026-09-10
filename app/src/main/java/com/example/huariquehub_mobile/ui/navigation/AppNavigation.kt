package com.example.huariquehub_mobile.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.huariquehub_mobile.data.model.UserRole
import com.example.huariquehub_mobile.data.model.UserSession
import com.example.huariquehub_mobile.data.remote.SessionManager
import com.example.huariquehub_mobile.ui.screens.auth.AccessDeniedScreen
import com.example.huariquehub_mobile.ui.screens.auth.ForgotPasswordScreen
import com.example.huariquehub_mobile.ui.screens.auth.LoginScreen
import com.example.huariquehub_mobile.ui.screens.auth.RegisterScreen
import com.example.huariquehub_mobile.ui.screens.home.HomeScreen
import com.example.huariquehub_mobile.ui.screens.home.HuariqueDetailScreen
import com.example.huariquehub_mobile.ui.screens.home.MapScreen
import com.example.huariquehub_mobile.ui.screens.notifications.NotificationsScreen
import com.example.huariquehub_mobile.ui.screens.preferences.PreferencesScreen
import com.example.huariquehub_mobile.ui.screens.profile.ProfileScreen
import com.example.huariquehub_mobile.ui.screens.owner.CreateEditHuariqueScreen
import com.example.huariquehub_mobile.ui.screens.owner.CreateEditPromoScreen
import com.example.huariquehub_mobile.ui.screens.owner.OwnerDashboardScreen
import com.example.huariquehub_mobile.ui.screens.owner.OwnerPromosScreen
import com.example.huariquehub_mobile.ui.screens.subscription.SubscriptionScreen

/**
 * Constantes de rutas de navegación.
 * - Autenticación: login, registro y recuperación de contraseña.
 * - Explorador: home, mapa, detalle, notificaciones y preferencias.
 * - Propietario: dashboard, CRUD de huariques y promos, suscripción.
 */
object Routes {
    const val LOGIN            = "login"
    const val REGISTER         = "register"
    const val FORGOT_PASSWORD  = "forgot_password"
    const val NOT_OWNER        = "not_owner"
    const val HOME             = "home"
    const val HUARIQUE_DETAIL  = "huarique_detail/{huariqueId}"
    const val MAP              = "map"
    const val NOTIFICATIONS    = "notifications"
    const val PREFERENCES      = "preferences"
    const val PROFILE          = "profile"
    const val OWNER_DASHBOARD  = "owner_dashboard"
    const val OWNER_NEW        = "owner_new"
    const val OWNER_EDIT       = "owner_edit/{huariqueId}"
    const val OWNER_PROMOS     = "owner_promos"
    const val OWNER_NEW_PROMO  = "owner_new_promo"
    const val OWNER_EDIT_PROMO = "owner_edit_promo/{promoId}"
    const val SUBSCRIPTION     = "subscription"

    fun huariqueDetail(id: Int) = "huarique_detail/$id"
    fun ownerEdit(id: Int) = "owner_edit/$id"
    fun ownerEditPromo(id: Int) = "owner_edit_promo/$id"
}

/** Grafo principal de pantallas; el destino inicial es [Routes.LOGIN]. */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var session by remember { mutableStateOf<UserSession?>(null) }

    NavHost(navController = navController, startDestination = Routes.LOGIN) {

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { loggedSession ->
                    session = loggedSession
                    // App de dueños: solo los propietarios acceden al panel; a
                    // cualquier otra cuenta le mostramos un mensaje amable.
                    val dest = if (loggedSession.isOwner) Routes.OWNER_DASHBOARD else Routes.NOT_OWNER
                    navController.navigate(dest) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) },
                onForgotPassword = { navController.navigate(Routes.FORGOT_PASSWORD) }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { newSession ->
                    session = newSession
                    // El registro de esta app crea siempre una cuenta de dueño.
                    navController.navigate(Routes.OWNER_DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Routes.NOT_OWNER) {
            AccessDeniedScreen(
                onLogout = {
                    SessionManager.clear()
                    session = null
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onHuariqueClick = { navController.navigate(Routes.huariqueDetail(it)) },
                onProfileClick = { navController.navigate(Routes.PROFILE) },
                onMapClick = { navController.navigate(Routes.MAP) },
                onNotificationsClick = { navController.navigate(Routes.NOTIFICATIONS) },
                onPreferencesClick = { navController.navigate(Routes.PREFERENCES) },
                userRole = session?.role ?: UserRole.CONSUMER
            )
        }

        composable(Routes.MAP) {
            MapScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PREFERENCES) {
            PreferencesScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    session = null
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onGoToSubscription = { navController.navigate(Routes.SUBSCRIPTION) },
                onGoToOwnerDashboard = { navController.navigate(Routes.OWNER_DASHBOARD) }
            )
        }

        composable(
            route = Routes.HUARIQUE_DETAIL,
            arguments = listOf(navArgument("huariqueId") { type = NavType.IntType })
        ) { back ->
            HuariqueDetailScreen(
                huariqueId = back.arguments?.getInt("huariqueId") ?: 1,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.OWNER_DASHBOARD) {
            OwnerDashboardScreen(
                ownerId = session?.id ?: 2,
                onProfile = { navController.navigate(Routes.PROFILE) },
                onAddHuarique = { navController.navigate(Routes.OWNER_NEW) },
                onEditHuarique = { navController.navigate(Routes.ownerEdit(it)) },
                onManagePromos = { navController.navigate(Routes.OWNER_PROMOS) },
                onSubscription = { navController.navigate(Routes.SUBSCRIPTION) }
            )
        }

        composable(Routes.OWNER_PROMOS) {
            OwnerPromosScreen(
                ownerId = session?.id ?: 2,
                onBack = { navController.popBackStack() },
                onAddPromo = { navController.navigate(Routes.OWNER_NEW_PROMO) },
                onEditPromo = { navController.navigate(Routes.ownerEditPromo(it)) }
            )
        }

        composable(Routes.OWNER_NEW_PROMO) {
            CreateEditPromoScreen(
                promoId = null,
                ownerId = session?.id ?: 2,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.OWNER_EDIT_PROMO,
            arguments = listOf(navArgument("promoId") { type = NavType.IntType })
        ) { back ->
            CreateEditPromoScreen(
                promoId = back.arguments?.getInt("promoId"),
                ownerId = session?.id ?: 2,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(Routes.SUBSCRIPTION) {
            SubscriptionScreen(
                userId = session?.id ?: 1,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.OWNER_NEW) {
            CreateEditHuariqueScreen(
                huariqueId = null,
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.OWNER_EDIT,
            arguments = listOf(navArgument("huariqueId") { type = NavType.IntType })
        ) { back ->
            CreateEditHuariqueScreen(
                huariqueId = back.arguments?.getInt("huariqueId"),
                onBack = { navController.popBackStack() },
                onSave = { navController.popBackStack() }
            )
        }
    }
}
