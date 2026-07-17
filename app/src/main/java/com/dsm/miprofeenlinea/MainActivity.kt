package com.dsm.miprofeenlinea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.dsm.miprofeenlinea.data.local.SessionManager
import com.dsm.miprofeenlinea.navigation.NavigationWrapper
import com.dsm.miprofeenlinea.ui.theme.FirebaseAuthTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val auth = Firebase.auth
        val sessionManager = SessionManager(this)
        setContent {
            val navHostController = rememberNavController()
            FirebaseAuthTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationWrapper(
                        navHostController = navHostController,
                        auth = auth,
                        startDestination = when {
                            auth.currentUser == null -> "initial"
                            sessionManager.getRole() == "docente" -> "homeDocente"
                            sessionManager.getRole() == "alumno" -> "home"
                            else -> "initial"
                        }
                    )
                }
            }
        }
    }
}
