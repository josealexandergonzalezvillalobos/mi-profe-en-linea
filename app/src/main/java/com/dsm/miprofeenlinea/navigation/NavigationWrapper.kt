package com.dsm.miprofeenlinea.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dsm.miprofeenlinea.presentacion.initial.InitialScreen
import com.dsm.miprofeenlinea.presentacion.login.LoginScreen
import com.dsm.miprofeenlinea.presentacion.home.HomeScreen
import com.dsm.miprofeenlinea.presentacion.signup.SignUpScreen
import com.dsm.miprofeenlinea.presentacion.waiting.WaitingTeacherScreen
import com.dsm.miprofeenlinea.presentacion.chat.ChatScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavigationWrapper(
    navHostController: NavHostController,
    auth: FirebaseAuth
) {
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen(
                navigateToLogin={navHostController.navigate("logIn")},
                navigateToSignUp={navHostController.navigate("signUp")}
            )
        }

        composable("logIn") {
            LoginScreen(
                auth,
                navigateToSignUp={navHostController.navigate("signUp")},
                navigateToHome={navHostController.navigate("home")}
            )

        }

        composable("signUp") {
            SignUpScreen(auth)
        }

        composable("home") {
            HomeScreen(
                onRequestTeacherClick = { taskId ->

                    navHostController.navigate(
                        "waitingTeacher/$taskId"
                    )
                }
            )
        }

        composable("waitingTeacher/{taskId}") {
            val taskId =
                it.arguments?.getString("taskId") ?: ""
            WaitingTeacherScreen(
                taskId = taskId,
                navigateToChat = {
                    navHostController.navigate("chat")
                }
            )
        }

        composable("chat") {
            ChatScreen()
        }
    }
}
