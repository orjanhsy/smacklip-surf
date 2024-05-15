package no.uio.ifi.in2000.team8


import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import no.uio.ifi.in2000.team8.ui.daily.DailySurfAreaScreen
import no.uio.ifi.in2000.team8.ui.daily.DailySurfAreaScreenViewModel
import no.uio.ifi.in2000.team8.ui.home.HomeScreen
import no.uio.ifi.in2000.team8.ui.home.HomeScreenViewModel
import no.uio.ifi.in2000.team8.ui.info.InfoScreen
import no.uio.ifi.in2000.team8.ui.map.MapScreen
import no.uio.ifi.in2000.team8.ui.map.MapScreenViewModel
import no.uio.ifi.in2000.team8.ui.surfarea.SurfAreaScreen
import no.uio.ifi.in2000.team8.ui.surfarea.SurfAreaScreenViewModel
import no.uio.ifi.in2000.team8.ui.theme.AppTheme
import no.uio.ifi.in2000.team8.utils.NavigationManager
import no.uio.ifi.in2000.team8.utils.NetworkConnectivityObserver
import no.uio.ifi.in2000.team8.utils.viewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //maximum duration of splashscreen is 10 seconds
        var countDownFinished = false
        object : CountDownTimer(10000, 10000){
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                countDownFinished = true
            }
        }.start()

        //show splashscreen until data for oflfNext7Days is loaded or 10 seconds has passed
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                SmackLipApplication.container.stateFulRepo.ofLfForecast.value.forecasts.isEmpty() && !countDownFinished
            }
        }

        var connectedAtOnePoint = false //true if app has been connected to internet at some point during the session
        val connectivityObserver = NetworkConnectivityObserver(applicationContext) //check internet connection

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
                    //if the device is connected to internet, call SmackLipNavigation and update connectedAtOnePoint to true
                    if (isConnected) {
                        SmackLipNavigation()
                        connectedAtOnePoint = true
                    }else{
                        //if the app has been connected to internet, but then lost the connection
                        //a snack bar will show, but also the app with the loaded data can be used
                        if (connectedAtOnePoint){
                            ShowSnackBarAndNavigation()
                        }else{
                            //if the app has not been connected to internet, data has not been loaded to
                            //the app and only the snack bar will show
                            ShowSnackBar()
                        }
                    }
                }
            }
        }
    }
}

//Snack bar is shown on top of the current screen
@Composable
fun ShowSnackBarAndNavigation(){
    Column(){
        Row{
            ShowSnackBar()
        }
        Row{
            SmackLipNavigation()
        }

    }
}

//Snack bar for when internet connection is missing
@Composable
fun ShowSnackBar() {
    Column(modifier = Modifier
        .fillMaxWidth()){
        Snackbar(
            modifier = Modifier.padding(8.dp),
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
            HomeScreenViewModel(
                SmackLipApplication.container.stateFulRepo,
                SmackLipApplication.container.alertsRepo,
                SmackLipApplication.container.settingsRepo
            )
        }
    )

    val savm = viewModel<SurfAreaScreenViewModel>(
        factory = viewModelFactory {
            SurfAreaScreenViewModel(
                SmackLipApplication.container.stateFulRepo,
                SmackLipApplication.container.alertsRepo
            )
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