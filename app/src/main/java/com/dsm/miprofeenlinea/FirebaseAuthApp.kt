package com.dsm.miprofeenlinea

import android.app.Application
import android.util.Log
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class FirebaseAuthApp: Application()  {
    override fun onCreate() {
        super.onCreate()
        Log.d("FirebaseAuthApp", "onCreate: Application started")
        try {
            startKoin {
                androidLogger(Level.DEBUG)
                androidContext(this@FirebaseAuthApp)
                modules(
                )
            }

        }catch (e: Exception){
            Log.e("FirebaseAuthApp", "Error al iniciar Koin ", e)
        }
    }
}