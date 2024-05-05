package com.example.myapplication

import DailySurfAreaScreen
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.createDataStore
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.data.settings.SettingsSerializer
import com.example.myapplication.presentation.viewModelFactory
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.home.HomeScreenViewModel
import com.example.myapplication.ui.map.MapScreen
import com.example.myapplication.ui.map.MapScreenViewModel
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.settings.SettingsScreenViewModel
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.SurfAreaScreen
import com.example.myapplication.ui.theme.AppTheme



//TODO: vm skal ikke være sånn! Må ha en viewmodel factory, men slashscreen må ha tilgang på en viewmodel

class MainActivity : ComponentActivity() {
    private lateinit var homeViewModelFactory: HomeScreenViewModel.HomeScreenViewModelFactory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appContainer =(application as SmackLipApplication).container
        homeViewModelFactory = HomeScreenViewModel.HomeScreenViewModelFactory(appContainer)
        val viewModelFactory = SettingsScreenViewModel.SettingsViewModelFactory(appContainer)

        val homeScreenViewModel: HomeScreenViewModel by viewModels { homeViewModelFactory }
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                homeScreenViewModel.homeScreenUiState.value.loading
            }
        }
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        setContent {
            AppTheme {
                val isConnected by connectivityObserver.observe().collectAsState(
                    initial = false
                )
                //foreløpig kommentert ut
                /*
                val viewModel = viewModel<SettingsScreenViewModel>(
                    factory = viewModelFactory{
                        SettingsScreenViewModel(SmackLipApplication.container.smackLipRepository)
                    }
                )
                 */


                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isConnected) {
                        SmackLipNavigation(viewModelFactory, homeViewModelFactory)
                    }else{
                        ShowSnackBar()
                        if (isConnected) {
                            SmackLipNavigation(viewModelFactory, homeViewModelFactory)
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
fun SmackLipNavigation(viewModelFactory: SettingsScreenViewModel.SettingsViewModelFactory, homeViewModelFactory: HomeScreenViewModel.HomeScreenViewModelFactory){
    val navController = rememberNavController()
    NavigationManager.navController = navController
    val dsvm = viewModel<DailySurfAreaScreenViewModel>(
        factory = viewModelFactory {
            DailySurfAreaScreenViewModel() // send med argument
        }
    )

    NavHost(
        navController = navController,
        startDestination = "HomeScreen",

        ){
        composable("HomeScreen"){
            HomeScreen(homeScreenViewModelFactory = homeViewModelFactory, navController = navController)
        }
        composable("SurfAreaScreen/{surfArea}") { backStackEntry ->
            val surfArea = backStackEntry.arguments?.getString("surfArea") ?: ""
            SurfAreaScreen(surfAreaName = surfArea, navController = navController)
        }
        composable("DailySurfAreaScreen/{surfArea}/{dayIndex}") { backStackEntry ->
            val surfArea = backStackEntry.arguments?.getString("surfArea") ?: ""
            val dayIndex = backStackEntry.arguments?.getString("dayIndex")?.toInt() ?: 0 // TODO: Handle differently
            DailySurfAreaScreen(surfAreaName = surfArea, daysFromToday = dayIndex, dsvm, navController = navController)
        }
        composable("MapScreen"){
            MapScreen(mapScreenViewModel = MapScreenViewModel(), navController = navController)
        }
        composable("SettingsScreen") {
            SettingsScreen(navController = navController, viewModelFactory)
        }
    }
}