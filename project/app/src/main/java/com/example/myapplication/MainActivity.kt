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
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.map.MapScreen
import com.example.myapplication.ui.settings.SettingsScreen
import com.example.myapplication.ui.settings.SettingsScreenViewModel
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.SurfAreaScreen
import com.example.myapplication.ui.theme.AppTheme

/*
val Context.settingsStore: DataStore<Settings> by dataStore (
    fileName = "settings",
    serializer = SettingsSerializer()
)

 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        val viewModelFactory = SettingsScreenViewModel.SettingsViewModelFactory(
            (application as SmackLipApplication).container, SavedStateHandle()
        )
        setContent {
            AppTheme {
                val isConnected by connectivityObserver.observe().collectAsState(
                    initial = false
                )
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isConnected) {
                        SmackLipNavigation(viewModelFactory)
                    }else{
                        ShowSnackBar()
                        if (isConnected) {
                            SmackLipNavigation(viewModelFactory)
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
fun SmackLipNavigation(viewModelFactory: SettingsScreenViewModel.SettingsViewModelFactory){
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
                onNavigateToHomeScreen = {navController.navigate("HomeScreen")},
                onNavigateToSettingsScreen = {navController.navigate("SettingsScreen")}
            )
        }
        composable("MapScreen"){
            MapScreen(
                onNavigateToSurfAreaScreen = {
                    navController.navigate("SurfAreaScreen/$it")

                }
            )
        }
        composable("SettingsScreen") {
            SettingsScreen(viewModelFactory)
        }

    }
}