package com.example.myapplication

import DailySurfAreaScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.map.MapScreen
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.SurfAreaScreen
import com.example.myapplication.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SmackLipNavigation()
                }
            }
        }
    }
}

@Composable
fun SmackLipNavigation(){
    val navController = rememberNavController()
    NavigationManager.navController = navController
    val dsvm = DailySurfAreaScreenViewModel()
    NavHost(
        navController = navController,
        startDestination = "HomeScreen",

        ){
        composable("HomeScreen"){
            HomeScreen(){
                navController.navigate("SurfAreaScreen/$it")
            }
        }
        composable("SurfAreaScreen/{surfArea}") { backStackEntry ->
            val surfArea = backStackEntry.arguments?.getString("surfArea") ?: ""
            SurfAreaScreen(surfAreaName = surfArea)
        }
        composable("DailySurfAreaScreen/{surfArea}/{dayIndex}") { backStackEntry ->
            val surfArea = backStackEntry.arguments?.getString("surfArea") ?: ""
            val dayIndex = backStackEntry.arguments?.getString("dayIndex")?.toInt() ?: 0 // TODO: Handle differently
            DailySurfAreaScreen(surfAreaName = surfArea, daysFromToday = dayIndex, dsvm)

        }
        composable("BottomBar"){
            BottomBar(
                onNavigateToMapScreen = { navController.navigate("MapScreen")},
                onNavigateToHomeScreen = {navController.navigate("HomeScreen")}
            )
        }
        composable("MapScreen"){
            MapScreen(
                onNavigateToSurfAreaScreen = {
                    navController.navigate("SurfAreaScreen/$it")

                }
            )
        }

    }
}