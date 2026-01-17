package com.example.fhub.view.uicontroller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fhub.view.auth.LoginScreen
import com.example.fhub.view.auth.RegisterScreen
import com.example.fhub.view.dasboard.HomeScreen
import com.example.fhub.view.profile.ProfileScreen
import com.example.fhub.view.profile.EditProfileScreen
import com.example.fhub.view.klien.KlienListScreen
import com.example.fhub.view.klien.KlienFormScreen
import com.example.fhub.view.klien.KlienDetailScreen
import com.example.fhub.view.project.ProjectListScreen
import com.example.fhub.view.project.ProjectFormScreen
import com.example.fhub.view.project.ProjectDetailScreen
import com.example.fhub.view.invoice.InvoiceListScreen
import com.example.fhub.view.invoice.InvoiceFormScreen
import com.example.fhub.view.invoice.InvoiceDetailScreen
import com.example.fhub.view.route.DestinasiDetailInvoice
import com.example.fhub.view.route.DestinasiDetailKlien
import com.example.fhub.view.route.DestinasiDetailProject
import com.example.fhub.view.route.DestinasiEditInvoice
import com.example.fhub.view.route.DestinasiEditKlien
import com.example.fhub.view.route.DestinasiEditProfile
import com.example.fhub.view.route.DestinasiEditProject
import com.example.fhub.view.route.DestinasiEntryInvoice
import com.example.fhub.view.route.DestinasiEntryKlien
import com.example.fhub.view.route.DestinasiEntryProject
import com.example.fhub.view.route.DestinasiHome
import com.example.fhub.view.route.DestinasiListInvoice
import com.example.fhub.view.route.DestinasiListKlien
import com.example.fhub.view.route.DestinasiListProject
import com.example.fhub.view.route.DestinasiLogin
import com.example.fhub.view.route.DestinasiProfile
import com.example.fhub.view.route.DestinasiRegister
import com.example.fhub.view.route.DestinasiSplash
import com.example.fhub.view.splash.SplashScreen
import com.example.fhub.viewmodel.AuthViewModel
import com.example.fhub.viewmodel.DetailViewModel
import com.example.fhub.viewmodel.EditViewModel
import com.example.fhub.viewmodel.EntryViewModel
import com.example.fhub.viewmodel.HomeViewModel
import com.example.fhub.viewmodel.provider.PenyediaViewModel

@Composable
fun FhubApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = DestinasiSplash.route,
        modifier = modifier
    ) {
        // ========== SPLASH ==========
        composable(route = DestinasiSplash.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            SplashScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = { navController.navigate(DestinasiLogin.route) { popUpTo(DestinasiSplash.route) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(DestinasiHome.route) { popUpTo(DestinasiSplash.route) { inclusive = true } } }
            )
        }

        // ========== LOGIN ==========
        composable(route = DestinasiLogin.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(DestinasiRegister.route) },
                onLoginSuccess = { navController.navigate(DestinasiHome.route) { popUpTo(DestinasiLogin.route) { inclusive = true } } }
            )
        }

        // ========== REGISTER ==========
        composable(route = DestinasiRegister.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = { navController.navigate(DestinasiLogin.route) { popUpTo(DestinasiRegister.route) { inclusive = true } } },
                onNavigateBack = { navController.navigate(DestinasiLogin.route) { popUpTo(DestinasiRegister.route) { inclusive = true } } }
            )
        }

        // ========== HOME / DASHBOARD ==========
        composable(DestinasiHome.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    homeViewModel.setUserId(id = user.idUser, name = user.namaLengkap, business = user.namaBisnis)
                }
            }

            HomeScreen(
                viewModel = homeViewModel,
                onNavigateToKlien = { navController.navigate(DestinasiListKlien.route) },
                onNavigateToProject = { navController.navigate(DestinasiListProject.route) },
                onNavigateToInvoice = { navController.navigate(DestinasiListInvoice.route) },
                onNavigateToProfile = { navController.navigate(DestinasiProfile.route) },
                onNavigateToAddKlien = { navController.navigate(DestinasiEntryKlien.route) },
                onNavigateToAddProject = { navController.navigate(DestinasiEntryProject.route) },
                onNavigateToAddInvoice = { navController.navigate(DestinasiEntryInvoice.route) }
            )
        }

        // ========== PROFILE ==========
        composable(route = DestinasiProfile.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            ProfileScreen(
                viewModel = authViewModel,
                onHome = { navController.navigate(DestinasiHome.route) },
                onKlien = { navController.navigate(DestinasiListKlien.route) },
                onProject = { navController.navigate(DestinasiListProject.route) },
                onInvoice = { navController.navigate(DestinasiListInvoice.route) },
                onEdit = { navController.navigate(DestinasiEditProfile.route) },
                onLogout = { navController.navigate(DestinasiLogin.route) { popUpTo(0) { inclusive = true } } }
            )
        }

        composable(route = DestinasiEditProfile.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            EditProfileScreen(
                viewModel = authViewModel,
                onBack = { navController.navigateUp() }
            )
        }

        // ========== KLIEN ==========
        composable(route = DestinasiListKlien.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user -> homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis) }
            }

            KlienListScreen(
                viewModel = homeViewModel,
                onNavigateToDetail = { id -> navController.navigate("${DestinasiDetailKlien.route}/$id") },
                onNavigateToAdd = { navController.navigate(DestinasiEntryKlien.route) },
                onNavigateToHome = { navController.navigate(DestinasiHome.route) },
                onNavigateToProject = { navController.navigate(DestinasiListProject.route) },
                onNavigateToInvoice = { navController.navigate(DestinasiListInvoice.route) },
                onNavigateToProfile = { navController.navigate(DestinasiProfile.route) }
            )
        }

        composable(route = DestinasiEntryKlien.route) {
            val entryViewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()

            // Collect State UI Klien
            val uiStateKlien by entryViewModel.uiStateKlien.collectAsState()

            LaunchedEffect(authState.currentUser) { authState.currentUser?.let { user -> entryViewModel.setUserId(user.idUser) } }

            KlienFormScreen(
                isEdit = false,
                detailKlien = uiStateKlien.detailKlien,
                isEntryValid = uiStateKlien.isEntryValid,

                // Sambungkan State Sukses & Error ke Screen
                isSaveSuccess = uiStateKlien.isSaveSuccess,
                errorMessageId = uiStateKlien.errorMessageId,

                onValueChange = entryViewModel::updateUiStateKlien,
                onSaveClick = { entryViewModel.saveKlien() },

                // onNavigateBack tetap navigateUp, TAPI dia hanya dipanggil
                // oleh LaunchedEffect di dalam Screen jika isSaveSuccess = true
                onNavigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = DestinasiDetailKlien.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailKlien.itemIdArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiDetailKlien.itemIdArg) ?: 0
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val detailViewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(authState.currentUser) { authState.currentUser?.let { user -> detailViewModel.setUserId(user.idUser) } }

            KlienDetailScreen(
                klienId = id,
                viewModel = detailViewModel,
                onNavigateBack = { navController.navigateUp() },
                onNavigateToEdit = { id -> navController.navigate("${DestinasiEditKlien.route}/$id") }
            )
        }

        composable(
            route = DestinasiEditKlien.routeWithArgs,
            arguments = DestinasiEditKlien.arguments
        ) { backStackEntry ->

            val editViewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()

            // ✅ PERBAIKAN DI SINI: Ganti 'Unit' menjadi 'authState'
            // Agar dia memantau perubahan status login. Begitu user loaded, data langsung diambil.
            LaunchedEffect(authState) {
                authState.currentUser?.let { user ->
                    val idKlien = backStackEntry.arguments?.getInt(DestinasiEditKlien.itemIdArg) ?: 0
                    if (idKlien != 0) {
                        editViewModel.initData(userId = user.idUser, idKlien = idKlien)
                    }
                }
            }

            // Screen Edit (Kode bawah tetap sama)
            KlienFormScreen(
                isEdit = true,
                detailKlien = editViewModel.uiStateKlien.detailKlien,
                isEntryValid = editViewModel.uiStateKlien.isEntryValid,
                isSaveSuccess = editViewModel.uiStateKlien.isSaveSuccess,
                errorMessageId = editViewModel.uiStateKlien.errorMessageId,
                onValueChange = editViewModel::updateUiStateKlien,
                onSaveClick = { editViewModel.saveUpdateKlien() },
                onNavigateBack = { navController.navigateUp() }
            )
        }

        // ========== PROJECT ==========
        composable(route = DestinasiListProject.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(Unit) { authState.currentUser?.let { user -> homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis) } }

            ProjectListScreen(
                viewModel = homeViewModel,
                onDetail = { id -> navController.navigate("${DestinasiDetailProject.route}/$id") },
                onAdd = { navController.navigate(DestinasiEntryProject.route) },
                onHome = { navController.navigate(DestinasiHome.route) },
                onKlien = { navController.navigate(DestinasiListKlien.route) },
                onInvoice = { navController.navigate(DestinasiListInvoice.route) },
                onProfile = { navController.navigate(DestinasiProfile.route) }
            )
        }

        composable(route = DestinasiEntryProject.route) {
            val entryViewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val homeUiState by homeViewModel.uiState.collectAsState()

            // ✅ PERBAIKAN: Collect StateFlow
            val uiStateProject by entryViewModel.uiStateProject.collectAsState()

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    entryViewModel.setUserId(user.idUser)
                    homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis)
                }
            }

            ProjectFormScreen(
                isEdit = false,
                detailProject = uiStateProject.detailProject, // ✅ Akses via variabel state
                klienList = homeUiState.listKlien,
                onValueChange = entryViewModel::updateUiStateProject,
                onSaveClick = { entryViewModel.saveProject() },
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = DestinasiDetailProject.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailProject.itemIdArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiDetailProject.itemIdArg) ?: 0
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val detailViewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(authState.currentUser) { authState.currentUser?.let { user -> detailViewModel.setUserId(user.idUser) } }

            ProjectDetailScreen(
                id = id,
                viewModel = detailViewModel,
                onBack = { navController.navigateUp() },
                onEdit = { id -> navController.navigate("${DestinasiEditProject.route}/$id") }
            )
        }

        composable(
            route = DestinasiEditProject.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditProject.itemIdArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiEditProject.itemIdArg) ?: 0
            val editViewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val homeUiState by homeViewModel.uiState.collectAsState()

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    editViewModel.initData(userId = user.idUser, idProject = id)
                    homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis)
                }
            }

            ProjectFormScreen(
                isEdit = true,
                detailProject = editViewModel.uiStateProject.detailProject,
                klienList = homeUiState.listKlien,
                onValueChange = editViewModel::updateUiStateProject,
                onSaveClick = { editViewModel.saveUpdateProject() },
                onBack = { navController.navigateUp() }
            )
        }

        // ========== INVOICE ==========
        composable(route = DestinasiListInvoice.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(authState.currentUser) { authState.currentUser?.let { user -> homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis) } }

            InvoiceListScreen(
                vm = homeViewModel,
                onDetail = { id -> navController.navigate("${DestinasiDetailInvoice.route}/$id") },
                onAdd = { navController.navigate(DestinasiEntryInvoice.route) },
                onHome = { navController.navigate(DestinasiHome.route) },
                onKlien = { navController.navigate(DestinasiListKlien.route) },
                onProject = { navController.navigate(DestinasiListProject.route) },
                onProfile = { navController.navigate(DestinasiProfile.route) }
            )
        }

        composable(route = DestinasiEntryInvoice.route) {
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val entryViewModel: EntryViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    entryViewModel.setUserId(user.idUser)
                    homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis)
                }
            }

            InvoiceFormScreen(
                entryViewModel = entryViewModel,
                editViewModel = null,
                homeViewModel = homeViewModel,
                onBack = { navController.navigateUp() }
            )
        }

        composable(
            route = DestinasiDetailInvoice.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailInvoice.itemIdArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val idInvoice = backStackEntry.arguments?.getInt(DestinasiDetailInvoice.itemIdArg) ?: 0
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()
            val detailViewModel: DetailViewModel = viewModel(factory = PenyediaViewModel.Factory)

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    detailViewModel.setUserId(user.idUser)
                }
            }

            InvoiceDetailScreen(
                id = idInvoice,
                viewModel = detailViewModel,
                user = authState.currentUser,
                onBack = { navController.navigateUp() },
                onEdit = { id -> navController.navigate("${DestinasiEditInvoice.route}/$id") }
            )
        }

        composable(
            route = DestinasiEditInvoice.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditInvoice.itemIdArg) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(DestinasiEditInvoice.itemIdArg) ?: 0
            val editViewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authViewModel: AuthViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val homeViewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
            val authState by authViewModel.uiState.collectAsState()

            LaunchedEffect(authState.currentUser) {
                authState.currentUser?.let { user ->
                    editViewModel.initData(userId = user.idUser, idInvoice = id)
                    homeViewModel.setUserId(user.idUser, user.namaLengkap, user.namaBisnis)
                }
            }

            InvoiceFormScreen(
                entryViewModel = null,
                editViewModel = editViewModel,
                homeViewModel = homeViewModel,
                onBack = { navController.navigateUp() }
            )
        }
    }
}