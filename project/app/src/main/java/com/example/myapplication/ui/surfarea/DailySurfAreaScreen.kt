
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CallMade
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.NavigationManager
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.HeaderCard
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.utils.RecourseUtils
import java.time.LocalTime

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySurfAreaScreen(surfAreaName: String, dailySurfAreaScreenViewModel: DailySurfAreaScreenViewModel = viewModel()) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!

    val dailySurfAreaScreenUiState by dailySurfAreaScreenViewModel.dailySurfAreaScreenUiState.collectAsState()


    dailySurfAreaScreenViewModel.updateOFLFNext7Days(surfArea = surfArea)
    dailySurfAreaScreenViewModel.updateWavePeriod(surfArea=surfArea)

    val navController = NavigationManager.navController


    Scaffold(
                topBar = {
                    TopAppBar(title = { /*TODO*/ },
                        navigationIcon = {
                            IconButton(onClick = { navController?.popBackStack() }) {
                                Column(
                                    modifier = Modifier
                                        .height(50.dp)
                                ) {
                                    Icon(
                                        Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.Black,
                                        modifier = Modifier
                                            .width(42.dp)
                                            .height(42.dp)
                                    )

                                }
                            }
                        }
                    )

                },
                bottomBar = {
                    BottomBar(
                        onNavigateToMapScreen = {
                            navController?.navigate("MapScreen")
                            //navigerer til mapscreen
                        },
                        onNavigateToHomeScreen = {
                            navController?.navigate("HomeScreen")
                            // Navigerer til HomeScreen
                        }
                    )
                }
            )
            { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        val currentHour = LocalTime.now().hour
                        var headerIcon = "default_icon"
                        val surfAreaDataForDay : Map<List<Int>, List<Any>> = dailySurfAreaScreenUiState.forecast7Days.getOrElse(0) { emptyMap() }
                        val times = surfAreaDataForDay.keys.sortedBy { it[3] }

                        if (surfAreaDataForDay.isNotEmpty()) {
                            // siden mappet ikke er sortert henter vi ut alle aktuelle tidspunketer og sorterer dem
                            for (time in times) {
                                val hour = time[3]
                                if (hour == currentHour) {
                                    headerIcon = surfAreaDataForDay[time]!![4].toString()
                                    break
                                }
                            }
                        }
                        HeaderCard(surfArea = surfArea, icon = headerIcon)
                    LazyColumn(
                        modifier = Modifier
                            .padding(5.dp)
                    ) {//vent dette er feil, dette er jo bare for i dag, må fikses med onclick

                        // [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]
                        if (surfAreaDataForDay.isNotEmpty()) {
                            items(times.size) { time ->
                                Log.d("hourindex","$time")
                                val hourIndex = times[time]
                                val surfAreaDataForHour :  List<Any>? = surfAreaDataForDay[hourIndex]
                                //henter objektet for timen som er en liste med Pair<List<Int>, Double>
                                val timestamp = hourIndex[3]
                                val windSpeed = surfAreaDataForHour?.get(0) ?: 0.0
                                val windGust = surfAreaDataForHour?.get(1)  ?: 0.0
                                val windDir = surfAreaDataForHour?.get(2)   ?: 0.0
                                val temp = surfAreaDataForHour?.get(3)      ?: 0.0
                                val icon = surfAreaDataForHour?.get(4)      ?: 0.0
                                val waveHeight = surfAreaDataForHour?.get(5)?: 0.0
                                val waveDir = surfAreaDataForHour?.get(6)   ?: 0.0
                                val waveperiod = try {dailySurfAreaScreenUiState.wavePeriods[hourIndex[3]]} catch (e: IndexOutOfBoundsException) {0.0}
                                Log.d("period","$waveperiod")

                                Log.d("timestamp", "$timestamp")
                                AllInfoCard(
                                    timestamp = timestamp.toString(),
                                    surfArea = surfArea,
                                    waveHeight = waveHeight,
                                    windSpeed = windSpeed,
                                    windGust = windGust,
                                    windDir = windDir,
                                    waveDir = waveDir,
                                    temp = temp,
                                    icon = icon,
                                    wavePeriod = waveperiod

                                )
                            }
                        } else {
                            item(7) {
                                AllInfoCard(
                                    timestamp = "00",
                                    surfArea = surfArea,
                                    waveHeight = 0.0,
                                    windSpeed = 0.0,
                                    windGust = 0.0,
                                    windDir = 0.0,
                                    waveDir = 0.0,
                                    temp = 0,
                                    icon = 0,
                                    wavePeriod = 0.0
                                )
                            }
                        }
                    }

                }
            }
        }



@Composable
fun AllInfoCard(
    timestamp: String,
    surfArea: SurfArea,
    waveHeight: Any,
    windSpeed: Any,
    windGust: Any,
    windDir: Any,
    waveDir: Any,
    temp : Any,
    icon: Any,
    wavePeriod: Double?
) {
    val recourseUtils : RecourseUtils = RecourseUtils()
    Card(
        modifier = Modifier
            .padding(3.dp)
            .fillMaxWidth()
            .height(49.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$timestamp",
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9A938C),
                ),
                modifier = Modifier.padding(4.dp)
            )

            Icon(
                imageVector = Icons.Outlined.Air,
                contentDescription = "air",
                modifier = Modifier
                    //.fillMaxSize()
                    .width(20.dp)
                    .height(20.dp)
            )

            Text(
                text = "$windSpeed (${windGust})",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Icon(
                imageVector = Icons.Outlined.CallMade,
                contentDescription = "arrow",
                modifier = Modifier
                    //.fillMaxSize()
                    .width(17.dp)
                    .height(17.dp)
            )

            Text(
                text = "$windDir",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Icon(
                imageVector = Icons.Outlined.Tsunami,
                contentDescription = "arrow",
                modifier = Modifier
                    //.fillMaxSize()
                    .width(18.dp)
                    .height(18.dp)
            )

            Text(
                text = "$waveHeight m",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Text(
                text = "$wavePeriod sek",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Icon(
                imageVector = Icons.Outlined.CallMade,
                contentDescription = "arrow",
                modifier = Modifier
                    //.fillMaxSize()
                    .width(17.dp)
                    .height(17.dp)
            )
            Text(
                text = "$waveDir",
                style = TextStyle(
                    fontSize = 13.sp,
                    lineHeight = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Text(
                text = "$temp",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Image(
                painter = painterResource(id = recourseUtils.findWeatherSymbol(icon.toString())),
                contentDescription = "image description",
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .aspectRatio(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewDailyScreen() {
    MyApplicationTheme {
        DailySurfAreaScreen("Hoddevik")
    }
}
