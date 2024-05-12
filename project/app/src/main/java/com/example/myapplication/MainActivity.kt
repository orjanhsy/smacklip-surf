package com.example.myapplication

//import androidx.datastore.preferences.createDataStore

import DailySurfAreaScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.presentation.viewModelFactory
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.home.HomeScreenViewModel
import com.example.myapplication.ui.info.InfoScreen
import com.example.myapplication.ui.info.InfoScreenViewModel
import com.example.myapplication.ui.map.MapScreen
import com.example.myapplication.ui.map.MapScreenViewModel
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.SurfAreaScreen
import com.example.myapplication.ui.surfarea.SurfAreaScreenViewModel
import com.example.myapplication.ui.theme.AppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen().apply {
            setKeepOnScreenCondition{
                SmackLipApplication.container.stateFulRepo.ofLfNext7Days.value.next7Days.isEmpty()
            }
        }

        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        setContent {
            val isDarkTheme by SmackLipApplication.container.infoViewModel.isDarkThemEnabled.collectAsState(initial = false)
            AppTheme( useDarkTheme = isDarkTheme) {
                val isConnected by connectivityObserver.observe().collectAsState(
                    initial = false
                )

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isConnected) {
                        SmackLipNavigation()
                    }else{
                        ShowSnackBar()
                        if (isConnected) {
                            SmackLipNavigation()
                        }
                    }
                }

            }
        }
    }

}


@Composable
fun ShowSnackBar() {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)){
        Snackbar(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = "Vennligst koble til internett.")
        }
    }

}

@Composable
fun SmackLipNavigation() {
    val navController = rememberNavController()
    NavigationManager.navController = navController

    //viewmodels
    val dsvm = viewModel<DailySurfAreaScreenViewModel>(
        factory = viewModelFactory {
            DailySurfAreaScreenViewModel(SmackLipApplication.container.stateFulRepo)
        }
    )

    val hsvm = viewModel<HomeScreenViewModel>(
        factory = viewModelFactory {
            HomeScreenViewModel(SmackLipApplication.container.stateFulRepo)
        }
    )

    val savm = viewModel<SurfAreaScreenViewModel>(
        factory = viewModelFactory {
            SurfAreaScreenViewModel(SmackLipApplication.container.stateFulRepo)
        }
    )

    val mapVm = viewModel<MapScreenViewModel>(
        factory = viewModelFactory {
            MapScreenViewModel(SmackLipApplication.container.stateFulRepo)
        }
    )

    //navigation
    NavHost(
        navController = navController,
        startDestination = "HomeScreen",

        ){
        composable("HomeScreen"){
            HomeScreen(hsvm, navController)
        }
        composable("SurfAreaScreen/{surfArea}") { backStackEntry ->
            val surfArea = backStackEntry.arguments?.getString("surfArea") ?: ""
            SurfAreaScreen(surfAreaName = surfArea, savm, navController)
        }
        composable("DailySurfAreaScreen/{surfArea}/{dayIndex}") { backStackEntry ->
            val surfArea = backStackEntry.arguments?.getString("surfArea") ?: ""
            val dayIndex = backStackEntry.arguments?.getString("dayIndex")?.toInt() ?: 0 // TODO: Handle differently
            DailySurfAreaScreen(surfAreaName = surfArea, dayOfMonth = dayIndex, dsvm, navController)
        }
        composable("MapScreen"){
            MapScreen(mapScreenViewModel = mapVm, navController =  navController)
        }
        composable("InfoScreen") {

            InfoScreen(SmackLipApplication.container.infoViewModel, navController)
        }
    }
}