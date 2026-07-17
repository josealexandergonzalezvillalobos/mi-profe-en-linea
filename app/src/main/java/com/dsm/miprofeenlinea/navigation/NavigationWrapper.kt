package com.dsm.miprofeenlinea.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dsm.miprofeenlinea.presentacion.chart.ChartScreen
import com.dsm.miprofeenlinea.presentacion.initial.InitialScreen
import com.dsm.miprofeenlinea.presentacion.login.LoginScreen
import com.dsm.miprofeenlinea.presentacion.home.HomeScreen
import com.dsm.miprofeenlinea.presentacion.signup.SignUpScreen
import com.dsm.miprofeenlinea.presentacion.waiting.WaitingTeacherScreen
import com.dsm.miprofeenlinea.presentacion.chat.ChatScreen
import com.google.firebase.auth.FirebaseAuth
import com.dsm.miprofeenlinea.presentacion.home.HomeDocenteScreen
import com.dsm.miprofeenlinea.presentacion.profile.ProfileScreen
import com.dsm.miprofeenlinea.presentacion.rating.RatingScreen

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth,
    startDestination: String = "initial"
) {
    NavHost(navController = navHostController, startDestination = startDestination) {
        composable("initial") {
            InitialScreen(
                navigateToLogin={navHostController.navigate("logIn")},
                navigateToSignUp={navHostController.navigate("signUp")}
            )
        }

        composable("logIn") {
            LoginScreen(
                auth,
                navigateToSignUp = {
                    navHostController.navigate("signUp")
                },
                navigateToHome = {
                    navHostController.navigate("home")
                },
                navigateToHomeDocente = {
                    navHostController.navigate("homeDocente")
                }
            )
        }

        composable("signUp") {
            SignUpScreen(auth)
        }

        composable("home") {
            HomeScreen(
                onGoToCharts = {
                    navHostController.navigate("charts/alumno")
                },
                onGoToProfile = {
                    navHostController.navigate("profile")
                },
                onRequestTeacherClick = { taskId ->
                    navHostController.navigate("waitingTeacher/$taskId")
                }
            )
        }

        composable("homeDocente") {
            HomeDocenteScreen(
                onGoToCharts = {
                    navHostController.navigate("charts/docente")
                },
                onGoToProfile = {
                    navHostController.navigate("profile")
                },
                onGoToChat = { taskId ->
                    navHostController.navigate("chat/$taskId/docente")
                }
            )
        }

        composable("waitingTeacher/{taskId}") {
            val taskId =
                it.arguments?.getString("taskId") ?: ""
            WaitingTeacherScreen(
                taskId = taskId,
                navigateToChat = { taskId ->
                    navHostController.navigate("chat/$taskId/alumno")
                }
            )
        }

        composable("chat/{taskId}/{role}") { backStackEntry ->

            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val role = backStackEntry.arguments?.getString("role") ?: "alumno"

            ChatScreen(
                taskId = taskId,
                currentUserRole = role,
                onGoToRating = { id, isTeacher ->
                    navHostController.navigate("rating/$id/$isTeacher")
                }
            )
        }

        composable("rating/{taskId}/{isTeacher}") { backStackEntry ->

            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            val isTeacher = backStackEntry.arguments?.getString("isTeacher")?.toBoolean() ?: false

            RatingScreen(
                taskId = taskId,
                isTeacher = isTeacher,
                onGoHomeTeacher = {
                    navHostController.navigate("homeDocente") {
                        popUpTo(0)
                    }
                },
                onGoHomeStudent = {
                    navHostController.navigate("home") {
                        popUpTo(0)
                    }
                }
            )
        }

        composable("charts/{role}") { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "alumno"
            ChartScreen(
                userId = auth.currentUser?.uid.orEmpty(),
                role = role,
                onBack = { navHostController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                uid = auth.currentUser?.uid.orEmpty(),
                email = auth.currentUser?.email,
                onBack = { navHostController.popBackStack() }
            )
        }
    }
}
