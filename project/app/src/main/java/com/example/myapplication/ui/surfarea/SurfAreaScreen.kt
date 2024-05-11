package com.example.myapplication.ui.surfarea

//import androidx.compose.material.icons.outlined.Tsunami
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.SmackLipApplication
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.presentation.viewModelFactory
import com.example.myapplication.ui.AlertCard.CustomAlert
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.theme.AppTheme
import com.example.myapplication.ui.theme.AppTypography
import com.example.myapplication.ui.theme.outlineLight
import com.example.myapplication.utils.RecourseUtils
import com.example.myapplication.utils.DateUtils
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfAreaScreen(
    surfAreaName: String,
    surfAreaScreenViewModel: SurfAreaScreenViewModel,
    navController: NavController
) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!

    if (surfArea != SmackLipApplication.container.stateFulRepo.areaInFocus.collectAsState().value) {
        surfAreaScreenViewModel.updateLocation(surfArea)
    }

    val surfAreaScreenUiState: SurfAreaScreenUiState by surfAreaScreenViewModel.surfAreaScreenUiState.collectAsState()

    val alerts = surfAreaScreenUiState.alertsSurfArea

    val formatter = DateTimeFormatter.ofPattern("EEE", Locale("no", "NO"))

    var showAlert by remember { mutableStateOf(false) }
    //var alertShowingNow by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { /*TODO*/ },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
                },
                actions = {
                    if (alerts.isNotEmpty()) {
                        IconButton(onClick = {
                            showAlert = true
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.icon_awareness_yellow_outlined),
                                contentDescription = "alert"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.inversePrimary)
            )
        },
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                val surfAreaDataForDay: Map<LocalDateTime, DataAtTime> = try {
                    surfAreaScreenUiState.forecastNext7Days.forecast[0].data
                } catch (e: IndexOutOfBoundsException) {
                    mapOf()
                }

                val currentHour =
                    LocalTime.now().hour // klokken er 10 så får ikke sjekket om det står 09 eller 9. Sto tidligere "08", "09" med .toString().padStart(2, '0')
                var headerIcon = ""

                if (surfAreaDataForDay.isNotEmpty()) {
                    // siden mappet ikke er sortert henter vi ut alle aktuelle tidspunketer og sorterer dem

                    val times = surfAreaDataForDay.keys.sortedWith(
                        compareBy<LocalDateTime> { it.month }.thenBy { it.dayOfMonth }
                    )

                    for (time in times) {
                        val hour = time.hour
                        if (hour == currentHour) {
                            headerIcon = surfAreaDataForDay[time]!!.symbolCode
                        }
                    }
                    HeaderCard(surfArea = surfArea, icon = headerIcon, LocalDateTime.now())
                } else {
                    HeaderCard(
                        surfArea = surfArea,
                        icon = R.drawable.spm.toString(),
                        LocalDateTime.now()
                    )
                }
            }
            item {
                LazyRow(
                    modifier = Modifier.padding(5.dp)
                ) {
                    if (surfAreaScreenUiState.forecastNext7Days.forecast.isNotEmpty()) {
                        val today = LocalDateTime.now()

                        items(surfAreaScreenUiState.forecastNext7Days.forecast.size) { dayIndex ->
                            val date = today.plusDays(dayIndex.toLong())
                            var formattedDate = formatter.format(date)


                                val conditionStatus: ConditionStatus = try {
                                    surfAreaScreenUiState.bestConditionStatusPerDay[dayIndex]
                                } catch (e: IndexOutOfBoundsException) {
                                    Log.d(
                                        "SAscreen",
                                        "ConditionStatus at day $dayIndex was out of bounds"
                                    )
                                    ConditionStatus.BLANK
                                } catch (e: NullPointerException) {
                                    Log.d("SAscreen", "ConditionStatus at day $dayIndex was null")
                                    ConditionStatus.BLANK
                                }
                            DayPreviewCard(
                                surfArea,
                                formattedDate,
                                Pair(
                                    surfAreaScreenUiState.minWaveHeights[dayIndex].toString(),
                                    surfAreaScreenUiState.maxWaveHeights[dayIndex].toString()
                                ),
                                conditionStatus,
                                date,
                                navController
                            )
                        }
                    } else {
                        items(6) { dayIndex ->
                            DayPreviewCard(
                                surfArea,
                                "man",
                                Pair("0.2", "1.3"),
                                ConditionStatus.BLANK,
                                LocalDateTime.now(),
                                null
                            )
                        }
                    }
                }
            }
            item {
                InfoCard(surfArea)
            }
        }
        if (alerts.isNotEmpty()) {
            ShowAlert(alerts = alerts,
                surfArea = surfArea,
                action = {})
        }

        if (showAlert) { //knappen i topappbar synes kun når det er farevarsel, demred er alerts ikke tom
            ShowAlert(alerts = alerts,
                surfArea = surfArea,
                action = {
                    showAlert = false
                })
        }
        //ProgressIndicator(isDisplayed = surfAreaScreenUiState.loading)
        // ProgressIndicator(isDisplayed = surfAreaScreenUiState.loading)
    }
}



@Composable
fun ShowAlert(alerts : List<Alert>, surfArea: SurfArea, action : () -> Unit){
    val dateFormatter: DateUtils = DateUtils();
    val alert = alerts.first()
    val time = dateFormatter.formatTimeInterval(alert.timeInterval?.interval)
    val alertMessage = alert.properties?.description ?: "No description available"
    val awarenessLevel = alert.properties?.awarenessLevel
    val icon = awarenessLevel?.let { getIconBasedOnAwarenessLevel(it) }
        ?: R.drawable.icon_awareness_default

    CustomAlert(
        title = surfArea.name,
        message = alertMessage,
        actionText = "OK",
        warningIcon = icon,
        time = time,
        data = null,
        showAlert = remember { mutableStateOf(true) },
        //actionWithValue = null,
        action = action,
    )
}


fun getIconBasedOnAwarenessLevel(awarenessLevel: String): Int {
    return try {
        if (awarenessLevel.isNotEmpty()) {
            val firstChar = awarenessLevel.firstOrNull()?.toString()

            when (firstChar) {
                "2" -> R.drawable.icon_warning_yellow
                "3" -> R.drawable.icon_warning_orange
                "4" -> R.drawable.icon_warning_red
                else -> R.drawable.icon_awareness_default // If awarenessLevel is not 2, 3, or 4
            }
        } else {
            R.drawable.icon_awareness_default
        }
    } catch (e: Exception) {
        R.drawable.icon_awareness_default
    }
}


@Composable
fun InfoCard(surfArea: SurfArea) {
    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .width(300.dp)
            .height(350.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, outlineLight) //BorderStroke(0.dp, Color.Transparent) //BorderStroke(2.dp, Color(0xFFBEC8CA))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inversePrimary)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = surfArea.locationName,
                    style = AppTypography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = stringResource(surfArea.description),
                    style = AppTypography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
                Image(
                    painter = painterResource(id = surfArea.image),
                    contentDescription = "Image",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .width(250.dp)
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
        }
    }
}

//calculate as some of the names are longer and needs to size the font down
@Composable
fun calculateFontSizeForText(text: String): TextUnit {
    val maxLength = 10 // Maximum length before font size reduction
    val defaultFontSize = 30.sp

    return if (text.length > maxLength) {
        val ratio = maxLength.toFloat() / text.length.toFloat()
        (defaultFontSize * ratio)
    } else {
        defaultFontSize
    }
}

@Composable
fun HeaderCard(surfArea: SurfArea, icon : String, date: LocalDateTime) {
    val formatter1 = DateTimeFormatter.ofPattern("E d. MMM", Locale("no", "NO"))
    val formattedDate1 = formatter1.format(date)

    //to get icon
    val recourseUtils : RecourseUtils = RecourseUtils()


    Box(
        modifier = Modifier
            .width(317.dp)
            .height(150.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Transparent,
            shape = RoundedCornerShape(size = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                ) {
                    Row {
                        Text(
                            text = surfArea.locationName + "," + "\n " + surfArea.areaName, //+surfArea.areaName //hadde vært fint med Stadt
                            style = TextStyle(
                                fontSize = calculateFontSizeForText(surfArea.locationName),
                                fontWeight = FontWeight(500),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = formattedDate1,
                            style = AppTypography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,

                            modifier = Modifier
                                .padding(5.dp)
                                .width(73.dp)
                                .height(16.dp)
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .shadow(
                            elevation = 37.425743103027344.dp,
                            spotColor = Color(0x0D000000),
                            ambientColor = Color(0x0D000000)
                        )
                        .padding(1.24752.dp)
                ) {
                    Image(
                        painter = painterResource(id = recourseUtils.findWeatherSymbol(icon)),
                        contentDescription = "image description",
                        modifier = Modifier
                            .width(126.dp)
                            .height(126.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DayPreviewCard(
    surfArea: SurfArea,
    day: String,
    waveHeightMinMax: Pair<String, String>,
    conditionStatus: ConditionStatus?,
    date: LocalDateTime,
    navController: NavController?
) {
    Card(
        modifier = Modifier
            .padding(5.dp)
            .width(98.dp)
            .height(120.dp)
            .clickable(
                onClick = {
                    navController?.navigate("DailySurfAreaScreen/${surfArea.locationName}/${date.dayOfMonth}")
                        ?: Unit
                }
            )
            .shadow(4.dp, shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),

        ) {
        Column(
            modifier = Modifier
                .padding(5.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "$day ${date.dayOfMonth}",
                    style = AppTypography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,

                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            )
            {
                //conditions -> copy from dailyScreen
                val surfBoard = when (conditionStatus) {
                    ConditionStatus.GREAT -> ConditionStatus.GREAT.surfBoard
                    ConditionStatus.DECENT -> ConditionStatus.DECENT.surfBoard
                    ConditionStatus.POOR -> ConditionStatus.POOR.surfBoard
                    ConditionStatus.BLANK -> ConditionStatus.BLANK.surfBoard
                    null -> R.drawable.blankboard
                }
                //surfboard icon
                Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 10.dp, bottom = 5.dp)) {
                    Image(
                        painter = painterResource(id = surfBoard),
                        contentDescription = "Weather Icon",
                        modifier = Modifier.size(30.dp),
                    )

                }
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = conditionStatus?.description ?: "",
                    style = AppTypography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .padding(0.03158.dp)
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Tsunami,
                            contentDescription = "tsunami",
                            modifier = Modifier
                                .fillMaxSize()
                                .width(40.dp)
                                .height(40.dp)
                        )

                    }
                }
                Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 3.dp)) {
                    Column {
                        Text(
                            text = "${waveHeightMinMax.first} - ${waveHeightMinMax.second}",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun PreviewSurfAreaScreen() {
    val savm = viewModel<SurfAreaScreenViewModel>(
        factory = viewModelFactory {
            SurfAreaScreenViewModel(SmackLipApplication.container.stateFulRepo)
        }
    )
    AppTheme {
        SurfAreaScreen("Solastranden", savm, rememberNavController())
        //DayPreviewCard()
        //HeaderCard()
        //InfoCard()
    }
}