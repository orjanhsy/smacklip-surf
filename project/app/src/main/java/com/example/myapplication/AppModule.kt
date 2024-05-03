package com.example.myapplication

import android.content.Context
import androidx.navigation.NavController
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl


interface AppModule {
    val stateFulRepo: Repository
    val navController: NavController?

}
class AppModuleImpl (
    private val appContext: Context,

): AppModule {
    override val stateFulRepo: Repository by lazy {
        RepositoryImpl()
    }

    override val navController: NavController by lazy {
        NavController(appContext)
    }
}