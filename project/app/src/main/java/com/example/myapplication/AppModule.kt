package com.example.myapplication

import android.content.Context
import com.example.myapplication.data.smackLip.Repository
import com.example.myapplication.data.smackLip.RepositoryImpl


interface AppModule {
    val stateFulRepo: Repository
}
class AppModuleImpl (
    private val appContext: Context

): AppModule {
    override val stateFulRepo: Repository by lazy {
        RepositoryImpl()
    }
}