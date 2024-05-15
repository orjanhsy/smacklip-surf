package com.example.myapplication.utils

import android.annotation.SuppressLint
import androidx.navigation.NavController

object NavigationManager {
    @SuppressLint("StaticFieldLeak")
    var navController: NavController? = null
}