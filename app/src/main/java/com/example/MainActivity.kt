package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.admin.AdminDashboardScreen
import com.example.ui.auth.SalonSignInScreen
import com.example.ui.components.IntroSplashGate
import com.example.ui.booking.BookingDialog
import com.example.ui.booking.NewBookingScreen
import com.example.ui.home.HomeScreen
import com.google.firebase.auth.FirebaseUser
import com.example.ui.menu.ConciergeDrawerContent
import com.example.ui.screens.AppointmentsScreen
import com.example.ui.screens.BoutiqueScreen
import com.example.ui.screens.ServicesCatalogScreen
import com.example.ui.screens.TeamDirectoryScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.DeepBlackBackground
import com.example.ui.theme.DeepBlackCard
import com.example.ui.theme.GoldChampagne
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SophisticatedWhite
import com.example.ui.theme.SuccessEmerald
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SalonViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Rotas de navegação da biblioteca androidx.navigation.compose.
 */
object SalonRoutes {
    const val HOME = "home"
    const val ADMIN = "admin"
    const val APPOINTMENTS = "appointments"
    const val SERVICES = "services"
    const val STYLISTS = "stylists"
    const val BOUTIQUE = "boutique"
    const val NEW_BOOKING = "new_booking"
    const val PROFILE = "profile"
}

fun AppScreen.toRoute(): String = when (this) {
    AppScreen.HOME -> SalonRoutes.HOME
    AppScreen.ADMIN -> SalonRoutes.ADMIN
    AppScreen.APPOINTMENTS -> SalonRoutes.APPOINTMENTS
    AppScreen.SERVICES -> SalonRoutes.SERVICES
    AppScreen.STYLISTS -> SalonRoutes.STYLISTS
    AppScreen.BOUTIQUE -> SalonRoutes.BOUTIQUE
    AppScreen.NEW_BOOKING -> SalonRoutes.NEW_BOOKING
    AppScreen.PROFILE -> SalonRoutes.PROFILE
}

fun routeToAppScreen(route: String?): AppScreen = when (route) {
    SalonRoutes.ADMIN -> AppScreen.ADMIN
    SalonRoutes.APPOINTMENTS -> AppScreen.APPOINTMENTS
    SalonRoutes.SERVICES -> AppScreen.SERVICES
    SalonRoutes.STYLISTS -> AppScreen.STYLISTS
    SalonRoutes.BOUTIQUE -> AppScreen.BOUTIQUE
    SalonRoutes.NEW_BOOKING -> AppScreen.NEW_BOOKING
    SalonRoutes.PROFILE -> AppScreen.PROFILE
    else -> AppScreen.HOME
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val viewModel: SalonViewModel = viewModel(factory = SalonViewModel.factory(context))
                val currentUser by viewModel.currentUser.collectAsState()
                val isSigningIn by viewModel.isSigningIn.collectAsState()
                val authError by viewModel.authError.collectAsState()
                val showIntro by viewModel.showIntroAnimation.collectAsState()
                var splashCompleted by remember { mutableStateOf(false) }

                if (showIntro && !splashCompleted) {
                    IntroSplashGate(onFinished = { splashCompleted = true })
                } else {
                    if (currentUser == null) {
                        SalonSignInScreen(
                            isSigningIn = isSigningIn,
                            errorMessage = authError,
                            onSignInClick = { viewModel.signInWithGoogle(context) },
                            onDismissError = { viewModel.clearAuthError() }
                        )
                    } else {
                        SalonAppRoot(
                            viewModel = viewModel,
                            currentUser = currentUser
                        )
                    }
                }
            }
        }
    }
}

/**
 * Estrutura Raiz do Aplicativo de Gestão de Salão de Beleza de Alto Padrão.
 * Contém o Scaffold principal, o Menu Lateral expansível (Drawer), o NavHost com transições suaves
 * da biblioteca androidx.navigation.compose e a Identidade Visual de Luxo (Preto Profundo e Dourado Champanhe).
 */
@Composable
fun SalonAppRoot(
    viewModel: SalonViewModel,
    currentUser: FirebaseUser? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val services by viewModel.services.collectAsState()
    val stylists by viewModel.stylists.collectAsState()

    // NavController do androidx.navigation.compose
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: SalonRoutes.HOME
    val currentScreen = routeToAppScreen(currentRoute)

    // Sincroniza o estado do ViewModel com a rota ativa do Navigation Compose
    LaunchedEffect(currentScreen) {
        if (uiState.currentScreen != currentScreen) {
            viewModel.navigateTo(currentScreen)
        }
    }

    // Função centralizada de navegação com salvamento de estado e singleTop
    val onNavigateToScreen: (AppScreen) -> Unit = { targetScreen ->
        val targetRoute = targetScreen.toRoute()
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(SalonRoutes.HOME) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    // Estado do Menu Lateral expansível / recolhível (Drawer)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Intercepta o botão Voltar do dispositivo para fechar o Drawer quando aberto
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    // Suporte responsivo (Mobile, Tablet e Computador)
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlackBackground)
    ) {
        val isWideScreen = maxWidth >= 840.dp

        if (isWideScreen) {
            // Layout expandido para Tablets / Desktops com menu lateral permanente
            PermanentNavigationDrawer(
                drawerContent = {
                    ConciergeDrawerContent(
                        currentScreen = currentScreen,
                        onNavigate = { screen -> onNavigateToScreen(screen) },
                        onCloseDrawer = { /* Menu permanente em telas largas */ },
                        onOpenBooking = { onNavigateToScreen(AppScreen.NEW_BOOKING) },
                        currentUser = currentUser,
                        onSignOut = { viewModel.signOut() },
                        modifier = Modifier.width(300.dp)
                    )
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepBlackBackground,
                    contentColor = SophisticatedWhite,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepBlackBackground)
                            .padding(innerPadding)
                    ) {
                        SalonNavHost(
                            navController = navController,
                            viewModel = viewModel,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToScreen = onNavigateToScreen,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        } else {
            // Layout móvel padrão com Menu Lateral expansível (ModalNavigationDrawer)
            ModalNavigationDrawer(
                drawerState = drawerState,
                scrimColor = Color(0xB3000000), // Efeito de escurecimento preto luxuoso
                drawerContent = {
                    ConciergeDrawerContent(
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            onNavigateToScreen(screen)
                            scope.launch { drawerState.close() }
                        },
                        onCloseDrawer = { scope.launch { drawerState.close() } },
                        onOpenBooking = {
                            scope.launch { drawerState.close() }
                            onNavigateToScreen(AppScreen.NEW_BOOKING)
                        },
                        currentUser = currentUser,
                        onSignOut = { viewModel.signOut() }
                    )
                }
            ) {
                // Scaffold Principal com as cores de luxo
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = DeepBlackBackground,
                    contentColor = SophisticatedWhite,
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(DeepBlackBackground)
                            .padding(innerPadding)
                    ) {
                        SalonNavHost(
                            navController = navController,
                            viewModel = viewModel,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToScreen = onNavigateToScreen,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        // Notificação de feedback VIP em formato de pílula dourada com profundidade 3D
        AnimatedVisibility(
            visible = uiState.userNotification != null,
            enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp, start = 20.dp, end = 20.dp)
        ) {
            uiState.userNotification?.let { msg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(DeepBlackCard)
                        .border(1.dp, GoldChampagne, RoundedCornerShape(30.dp))
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessEmerald
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = SophisticatedWhite
                            )
                        )
                    }
                }

                LaunchedEffect(msg) {
                    delay(3500)
                    viewModel.clearNotification()
                }
            }
        }

        // Modal de Agendamento Interativo de Experiência VIP
        if (uiState.isBookingDialogOpen) {
            BookingDialog(
                services = services,
                stylists = stylists,
                preselectedService = uiState.preselectedService,
                preselectedStylist = uiState.preselectedStylist,
                onDismiss = { viewModel.closeBookingDialog() },
                onConfirm = { name, phone, isVip, service, stylist, time, notes ->
                    viewModel.confirmBooking(name, phone, isVip, service, stylist, time, notes)
                }
            )
        }
    }
}

/**
 * Gerenciador de Navegação (NavHost) do androidx.navigation.compose.
 * Configura transições suaves com fade e deslizamento lateral entre a Home, o Painel ADM
 * e todas as telas do aplicativo.
 */
@Composable
fun SalonNavHost(
    navController: NavHostController,
    viewModel: SalonViewModel,
    onOpenDrawer: () -> Unit,
    onNavigateToScreen: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = SalonRoutes.HOME,
        modifier = modifier,
        enterTransition = {
            fadeIn(animationSpec = tween(350)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(350)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(350)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(350)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(350)) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(350)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(350)) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(350)
            )
        }
    ) {
        composable(SalonRoutes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateToAdmin = { onNavigateToScreen(AppScreen.ADMIN) },
                onNavigateToAppointments = { onNavigateToScreen(AppScreen.APPOINTMENTS) },
                onNavigateToNewBooking = { onNavigateToScreen(AppScreen.NEW_BOOKING) }
            )
        }

        composable(SalonRoutes.NEW_BOOKING) {
            NewBookingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAppointments = {
                    navController.navigate(SalonRoutes.APPOINTMENTS) {
                        popUpTo(SalonRoutes.HOME) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(SalonRoutes.ADMIN) {
            AdminDashboardScreen(
                viewModel = viewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SalonRoutes.APPOINTMENTS) {
            AppointmentsScreen(
                viewModel = viewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewBooking = { onNavigateToScreen(AppScreen.NEW_BOOKING) }
            )
        }

        composable(SalonRoutes.SERVICES) {
            ServicesCatalogScreen(
                viewModel = viewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewBooking = { onNavigateToScreen(AppScreen.NEW_BOOKING) }
            )
        }

        composable(SalonRoutes.STYLISTS) {
            TeamDirectoryScreen(
                viewModel = viewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNewBooking = { onNavigateToScreen(AppScreen.NEW_BOOKING) }
            )
        }

        composable(SalonRoutes.BOUTIQUE) {
            BoutiqueScreen(
                viewModel = viewModel,
                onOpenDrawer = onOpenDrawer,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(SalonRoutes.PROFILE) {
            UserProfileScreen(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
