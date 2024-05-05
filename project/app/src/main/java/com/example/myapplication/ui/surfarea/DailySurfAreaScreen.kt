
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.NavigationManager
import com.example.myapplication.R
import com.example.myapplication.SmackLipApplication
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.presentation.viewModelFactory
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.HeaderCard
import com.example.myapplication.ui.theme.AppTheme
import com.example.myapplication.ui.theme.AppTypography
import com.example.myapplication.utils.RecourseUtils
import java.time.LocalDateTime
import java.time.LocalTime

@SuppressLint("SuspiciousIndentation", "DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySurfAreaScreen(
    surfAreaName: String,
    dayOfMonth: Int,
    dailySurfAreaScreenViewModel: DailySurfAreaScreenViewModel
) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!

    val dailySurfAreaScreenUiState by dailySurfAreaScreenViewModel.dailySurfAreaScreenUiState.collectAsState()

    if (dayOfMonth != SmackLipApplication.container.stateFulRepo.dayInFocus.collectAsState().value) {
        dailySurfAreaScreenViewModel.updateDayInFocus(dayOfMonth)
    }

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
                    BottomBar(navController = navController)
                }
            )
    { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val currentTime = LocalDateTime.now()
                val currentHour = LocalDateTime.now().hour
                val formattedCurrentHour = String.format("%02d", currentHour)
                var headerIcon = "default_icon"

                val surfAreaDataForDay: Map<LocalDateTime, DataAtTime> = try { // TODO: IndexOutOfBounds vil aldri skje
                    dailySurfAreaScreenUiState.dataAtDay.data
                } catch (e: IndexOutOfBoundsException) {
                    mapOf()
                }


                val times = surfAreaDataForDay.keys.sortedWith(
                    compareBy<LocalDateTime> { it.month }.thenBy { it.dayOfMonth }
                )

                if (surfAreaDataForDay.isNotEmpty()) {
                    // siden mappet ikke er sortert henter vi ut alle aktuelle tidspunketer og sorterer dem
                    for (time in times) {
                        val hour = time.hour
                        if (hour == formattedCurrentHour.toInt()) {
                            headerIcon = surfAreaDataForDay[time]!!.symbolCode //TODO: !!
                            break
                        }
                    }
                }
                val headerTime = try { times[0] }
                catch (e: IndexOutOfBoundsException) { LocalDateTime.now() } // TODO: bedre catch?

                HeaderCard(surfArea = surfArea, icon = headerIcon, headerTime)
                LazyColumn(
                    modifier = Modifier
                        .padding(5.dp)
                ) {//vent dette er feil, dette er jo bare for i dag, må fikses med onclick

                    // [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]
                    if (surfAreaDataForDay.isNotEmpty()) {
                        items(times.size) { index ->
                            val time = times[index]
                            val hour = time.hour
                            if (time.dayOfMonth == currentTime.dayOfMonth && time.hour < currentTime.hour) { // håndterer forecasts 2 timer før "nå"
                                AllInfoCard(surfArea = surfArea)
                            } else {
                                val formattedHour = String.format("%02d", hour)

                                // TODO: ?
                                val surfAreaDataForHour: DataAtTime? = surfAreaDataForDay[time]
                                //henter objektet for timen som er en liste med Pair<List<Int>, Double>

                                val windSpeed = surfAreaDataForHour?.windSpeed ?: 0.0
                                val windGust = surfAreaDataForHour?.windGust ?: 0.0
                                val windDir = surfAreaDataForHour?.windDir ?: 0.0
                                val temp = surfAreaDataForHour?.airTemp ?: 0.0
                                val icon = surfAreaDataForHour?.symbolCode ?: 0.0
                                val waveHeight = surfAreaDataForHour?.waveHeight ?: 0.0
                                val waveDir = surfAreaDataForHour?.waveDir ?: 0.0

                                val wavePeriod = try {
                                    val currentWaveIndex =  if (hour < 23){
                                        times.indexOf(times.find { it.hour in hour .. hour+2})
                                    } else {
                                        times.indexOf(times.find { it.hour in listOf(hour, hour + 1 % 24, hour + 2 % 24)})
                                    }
                                    dailySurfAreaScreenUiState.wavePeriods[currentWaveIndex]
                                } catch (e: IndexOutOfBoundsException) {
                                    Log.d(
                                        "DSAscreen",
                                        "Waveperiods ${hour} out of bounds for waveperiods of size ${dailySurfAreaScreenUiState.wavePeriods.size}"
                                    )
                                    0.0
                                }

                                val conditionStatus: ConditionStatus? = try {
                                    dailySurfAreaScreenUiState.conditionStatuses[dayOfMonth][time]
                                } catch (e: IndexOutOfBoundsException) {
                                    null
                                }

                                AllInfoCard(
                                    timestamp = formattedHour,
                                    surfArea = surfArea,
                                    waveHeight = waveHeight,
                                    windSpeed = windSpeed,
                                    windGust = windGust,
                                    windDir = windDir,
                                    waveDir = waveDir,
                                    temp = temp,
                                    icon = icon,
                                    wavePeriod = wavePeriod,
                                    conditionStatus = conditionStatus
                                )
                            }
                        }
                    } else {
                        item(7) {
                            AllInfoCard(surfArea = surfArea)
                        }
                    }
                }

            }
           // ProgressIndicator(isDisplayed = dailySurfAreaScreenUiState.loading)
        }
    }
}

// TODO: tror type kan være noe annet enn default, og de kan ha andre default values
@Composable
fun AllInfoCard(
    timestamp: String = "00",
    surfArea: SurfArea,
    waveHeight: Any = 0.0,
    windSpeed: Any = 0.0,
    windGust: Any = 0.0,
    windDir: Any = 0.0,
    waveDir: Any = 0.0,
    temp: Any = 0,
    icon: Any = 0,
    wavePeriod: Double? = 0.0,
    conditionStatus: ConditionStatus? = ConditionStatus.BLANK
) {

    if (timestamp != "00") {

        val recourseUtils: RecourseUtils = RecourseUtils()

        // winddir
        val rotationAngleWind = when (windDir) {
            is Double -> windDir.toFloat()
            is Int -> windDir.toFloat()
            else -> 0f
        }

        // wavedir
        val rotationAngleWaveDir = when (waveDir) {
            is Double -> waveDir.toFloat()
            is Int -> waveDir.toFloat()
            else -> 0f
        }

        Card(
            modifier = Modifier
                .padding(3.dp)
                .fillMaxWidth()
                .height(49.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = timestamp,
                    style = AppTypography.bodySmall,
                    modifier = Modifier.weight(1f)
                )

                // Wind Group
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Air,
                        contentDescription = "Air",
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${(windSpeed as Double).toInt()} (${(windGust as Double).toInt()})",
                        style = AppTypography.bodySmall,
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Outlined.CallMade,
                        contentDescription = "Arrow",
                        modifier = Modifier
                            .size(17.dp)
                            .rotate(rotationAngleWind - 45)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Wave Group
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Tsunami,
                        contentDescription = "Tsunami",
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "$waveHeight m",
                        style = AppTypography.bodySmall,
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "${wavePeriod?.toInt()} sek",
                        style = AppTypography.bodySmall,
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Outlined.CallMade,
                        contentDescription = "Arrow",
                        modifier = Modifier
                            .size(17.dp)
                            .rotate(rotationAngleWaveDir - 45)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Temp
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (temp is Double) {
                            temp.toInt().toString()
                        } else {
                            temp.toString()
                        },
                        style = AppTypography.bodySmall,
                        modifier = Modifier.padding(end = 4.dp)
                    )

                    Image(
                        painter = painterResource(id = recourseUtils.findWeatherSymbol(icon.toString())),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                val surfBoard = when (conditionStatus) {
                    ConditionStatus.GREAT -> ConditionStatus.GREAT.surfBoard
                    ConditionStatus.DECENT -> ConditionStatus.DECENT.surfBoard
                    ConditionStatus.POOR -> ConditionStatus.POOR.surfBoard
                    ConditionStatus.BLANK -> ConditionStatus.BLANK.surfBoard
                    null -> R.drawable.spm
                }

                Image(
                    painter = painterResource(id = surfBoard),
                    contentDescription = "Weather Icon",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
//@Preview(showBackground = true, name = "Dark Mode")
@Preview(showBackground = true, name = "Light Mode")
@Composable
private fun PreviewDailyScreen() {
    val dsvm = viewModel<DailySurfAreaScreenViewModel>(
        factory = viewModelFactory {
            DailySurfAreaScreenViewModel(SmackLipApplication.container.stateFulRepo)
        }
    )
    AppTheme (darkTheme = false){
        DailySurfAreaScreen("Hoddevik", 5, dsvm)
    }
}
