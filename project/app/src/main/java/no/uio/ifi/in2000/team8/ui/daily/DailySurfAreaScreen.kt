package no.uio.ifi.in2000.team8.ui.daily
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team8.R
import no.uio.ifi.in2000.team8.SmackLipApplication
import no.uio.ifi.in2000.team8.model.conditions.ConditionStatus
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.model.weatherforecast.DataAtTime
import no.uio.ifi.in2000.team8.ui.common.composables.BottomBar
import no.uio.ifi.in2000.team8.ui.common.composables.HeaderCard
import no.uio.ifi.in2000.team8.ui.theme.AppTheme
import no.uio.ifi.in2000.team8.ui.theme.AppTypography
import no.uio.ifi.in2000.team8.utils.ResourceUtils
import no.uio.ifi.in2000.team8.utils.viewModelFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailySurfAreaScreen(
    surfAreaName: String,
    dayOfMonth: Int,
    dailySurfAreaScreenViewModel: DailySurfAreaScreenViewModel,
    navController: NavController
) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!

    val dailySurfAreaScreenUiState by dailySurfAreaScreenViewModel.dailySurfAreaScreenUiState.collectAsState()

    //update dayInFocus in WeatherForecastRepository
    if (dayOfMonth != SmackLipApplication.container.stateFulRepo.dayInFocus.collectAsState().value) {
        dailySurfAreaScreenViewModel.updateDayInFocus(dayOfMonth)
    }

    Scaffold(
        topBar = { //topbar contains back-arrow,
            TopAppBar(title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Column(
                            modifier = Modifier
                                .height(50.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .width(42.dp)
                                    .height(42.dp)
                            )

                        }
                    }
                },
                /*
                This causes IDE-warning as smallTopAppBarColors is deprecated.
                We made the decision to keep it as there was no alternative worth our time.
                 */
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
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
                //formatting time
                val currentTime = LocalDateTime.now()
                val currentDay = currentTime.toLocalDate()
                val currentHour = currentTime.hour

                //getting weather-icon for headercard based on formatted current time
                var headerIcon = "default_icon"
                val headerTime: LocalDateTime

                //variable for viewmodel method, getting map of daily data
                val surfAreaDataForDay: Map<LocalDateTime, DataAtTime> = dailySurfAreaScreenUiState.dataAtDay.data
                //variable used to match hour to data
                val times = surfAreaDataForDay.keys.sortedBy { it.hour }

                // header weather-icon for current hour. On "todays" surfArea the weathericon matches the hpur.
                //On next 8 days the icon shows the midday icon
                //icons are METs own icons, used by services such as Yr:
                if (surfAreaDataForDay.isNotEmpty()){
                    val timesForToday = times.filter {
                        it.toLocalDate() == currentDay
                    }
                    if (timesForToday.isNotEmpty()){
                        headerTime = timesForToday.find { it.hour >= currentHour } ?: timesForToday.first()
                        headerIcon = surfAreaDataForDay[headerTime]?.symbolCode ?: headerIcon
                    // icon for time in the middle of the day for remaining days
                    } else{
                        val futureDaysMap = surfAreaDataForDay.keys.groupBy { it.toLocalDate() }
                            .filterKeys { it.isAfter(currentDay) }
                        val nextDayTimes = futureDaysMap.entries.firstOrNull()?.value?.sorted() ?: emptyList()
                        // checking if more than 1 hour is remaining and then finding icon for middle hour
                        headerTime = if (nextDayTimes.size > 1) nextDayTimes[nextDayTimes.size / 2] else nextDayTimes.firstOrNull() ?: currentTime
                        headerIcon = surfAreaDataForDay[headerTime]?.symbolCode ?: headerIcon
                    }
                } else {
                    headerTime = currentTime
                }

                HeaderCard(surfArea = surfArea, icon = headerIcon, headerTime)
                LazyColumn( //hour by hour displayed in scrollable cards
                    modifier = Modifier
                        .padding(5.dp)
                ) {
                        val hourFormatter = DateTimeFormatter.ofPattern("HH") //two digit hour, example: not 9 but 09
                        items(times.size) { index -> //getting remanining hours of day
                            val time = times[index] //time is the hour of the day
                            val hour = time.hour //Returns hour-of-day, from 0 to 23
                            //matching hour formatting
                            val formattedHour = time.format(hourFormatter)

                            val surfAreaDataForHour: DataAtTime? = surfAreaDataForDay[time] //getting resepctive hour-data

                            val windSpeed = surfAreaDataForHour?.windSpeed ?: 0.0
                            val windGust = surfAreaDataForHour?.windGust ?: 0.0
                            val windDir = surfAreaDataForHour?.windDir ?: 0.0
                            val temp = surfAreaDataForHour?.airTemp ?: 0.0
                            val icon = surfAreaDataForHour?.symbolCode ?: 0.0
                            val waveHeight = surfAreaDataForHour?.waveHeight ?: 0.0
                            val waveDir = surfAreaDataForHour?.waveDir ?: 0.0

                            val wavePeriod = try {
                                val waveIndex = times.indexOf(times.find { it.hour == hour})

                                dailySurfAreaScreenUiState.wavePeriods[waveIndex]

                            } catch (e: IndexOutOfBoundsException) {
                                -1.0 //gives < 0 value to deal with no waveperiod in UI
                            }

                            val conditionStatus: ConditionStatus? = try {
                                dailySurfAreaScreenUiState.conditionStatuses[time]
                            } catch (e: IndexOutOfBoundsException) {
                                null
                            }

                            AllInfoCard(
                                timestamp = formattedHour,
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
            }
        }
    }
}

@Composable
fun AllInfoCard(
    timestamp: String = "x",
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

    if (timestamp != "x") {

        val resourceUtils = ResourceUtils()

        // winddir, can be both int and double, handled here
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
                .height(49.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),

            ) {

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween //evenly spaced. also on tablet
            ) {

                //Displaying hour
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .padding(top = 4.dp) // add padding for alignment
                ) {
                    Text(
                        text = timestamp,
                        style = AppTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                // wind Group
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //air icon
                    Icon(
                        imageVector = Icons.Outlined.Air,
                        contentDescription = "Air",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    //wind text
                    Box(
                        modifier = Modifier
                            .size(width = 50.dp, height = 30.dp)
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = if (windGust as Double > windSpeed as Double) "${(windSpeed).toInt()} (${(windGust).toInt()})"
                            else "${(windSpeed).toInt()}",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                            //handles wind and wind + gust
                        )
                    }

                    //wind direction arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.CallMade,
                        contentDescription = "Arrow",
                        tint=  MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(17.dp)
                            .rotate(rotationAngleWind - 135) //adjust angle (180-45)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // wave Group
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //wave icon
                    Icon(
                        imageVector = Icons.Outlined.Tsunami,
                        contentDescription = "Tsunami",
                        tint=MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    //wave height text
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .padding(top = 8.dp)
                    ) {
                        Text(
                            text = "$waveHeight m",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    //wave period text
                    if (wavePeriod != null && wavePeriod >= 0) { //waveperiod for first 3 days, remaning days don´t dispaly waveperiod

                        Text(
                            text = "${wavePeriod.toInt()} sek",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "-- sek",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    //wave direction arrow
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.CallMade,
                        contentDescription = "Arrow",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .size(17.dp)
                            .rotate(rotationAngleWaveDir - 135)
                    )
                }

                Spacer(modifier = Modifier.width(5.dp))

                // Weather group
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //temperature
                     Box(
                        modifier = Modifier
                            .size(width = 30.dp, height = 30.dp)
                            .padding(top = 6.dp)
                    ) {
                        Text(
                            text = if (temp is Double) {
                                val temperature = temp.toInt()
                                if (temperature < 10) {
                                    "  $temperature" //align single digit with 0 position
                                } else {
                                    "$temperature"
                                }
                            } else {
                                val temperature = temp.toString()
                                if (temperature.toInt() < 10) {
                                    "  $temperature" //align single digit with 1 position
                                } else {
                                    temperature
                                }
                            } + "\u00B0", //degrees symbol
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(end = 1.dp)
                        )
                    }
                    //Weather icon
                    Image(
                        painter = painterResource(id = resourceUtils.findWeatherSymbol(icon.toString())),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                //condition surfboard based on condition-algorithm
                val surfBoard = when (conditionStatus) {
                    ConditionStatus.GREAT -> ConditionStatus.GREAT.surfBoard
                    ConditionStatus.DECENT -> ConditionStatus.DECENT.surfBoard
                    ConditionStatus.POOR -> ConditionStatus.POOR.surfBoard
                    ConditionStatus.BLANK -> ConditionStatus.BLANK.surfBoard
                    null -> R.drawable.spm
                }
                //condition status
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
    AppTheme (useDarkTheme = false){
        DailySurfAreaScreen("Hoddevik", 5, dsvm, rememberNavController())
    }
}
