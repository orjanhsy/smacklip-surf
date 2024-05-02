package com.example.myapplication.ui.surfarea

//import androidx.compose.material.icons.outlined.Tsunami
import android.util.Log
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.myapplication.NavigationManager
import com.example.myapplication.R
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.AlertCard.CustomAlert
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.common.composables.ProgressIndicator
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SchemesSurface
import com.example.myapplication.utils.RecourseUtils
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfAreaScreen(
    surfAreaName: String,
    surfAreaScreenViewModel: SurfAreaScreenViewModel = viewModel(),
) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!


    val surfAreaScreenUiState: SurfAreaScreenUiState by surfAreaScreenViewModel.surfAreaScreenUiState.collectAsState()
    //starter loadingscreen i VM her
    surfAreaScreenViewModel.asyncNext7Days(surfArea)
    surfAreaScreenViewModel.updateWavePeriods(surfArea)
    surfAreaScreenViewModel.updateAlertsSurfArea(surfArea)
    //avslutter loadingscreen i VM her

    val alerts = surfAreaScreenUiState.alertsSurfArea


    val formatter = DateTimeFormatter.ofPattern("EEE", Locale("no", "NO"))
    val navController = NavigationManager.navController


    Scaffold(
        topBar = {
            TopAppBar(
                title = { /*TODO*/ },
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
                },
            )
        },
        bottomBar = {
            BottomBar(navController = navController
                /*onNavigateToMapScreen = {
                    navController?.navigate("MapScreen")
                },
                onNavigateToHomeScreen = {
                    navController?.navigate("HomeScreen")
                }*/
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),

                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    val surfAreaDataForDay: Map<List<Int>, List<Any>> =
                        surfAreaScreenUiState.forecastNext7Days.getOrElse(0) { emptyMap() } //0 is today
                    val currentHour =
                        LocalTime.now().hour // klokken er 10 så får ikke sjekket om det står 09 eller 9. Sto tidligere "08", "09" med .toString().padStart(2, '0')
                    var headerIcon = ""

                    if (surfAreaDataForDay.isNotEmpty()) {
                        // siden mappet ikke er sortert henter vi ut alle aktuelle tidspunketer og sorterer dem
                        val times = surfAreaDataForDay.keys.sortedBy { it[3] }
                        for (time in times) {
                            val hour = time[3]
                            if (hour == currentHour) {
                                headerIcon = surfAreaDataForDay[time]!![4].toString()
                            }
                        }
                        HeaderCard(surfArea = surfArea, icon = headerIcon, LocalDate.now())
                    }
                        else{
                            HeaderCard(surfArea = surfArea, icon = R.drawable.spm.toString(), LocalDate.now())
                    }
                }
                item {
                    LazyRow(
                        modifier = Modifier.padding(5.dp)
                    ) {
                        if (surfAreaScreenUiState.forecastNext7Days.isNotEmpty()) {
                            val today = LocalDate.now()
                            surfAreaScreenViewModel.updateBestConditionStatuses( //loading screen vises
                                surfArea,
                                surfAreaScreenUiState.forecastNext7Days
                            )

                            items(surfAreaScreenUiState.forecastNext7Days.size) { dayIndex ->
                                val date = today.plusDays(dayIndex.toLong())
                                val formattedDate = formatter.format(date)

                                val conditionStatus: ConditionStatus = try {
                                    surfAreaScreenUiState.bestConditionStatuses[dayIndex]!!
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
                                    dayIndex,
                                    navController
                                )
                            }
                        } else {
                            items(6) { dayIndex ->
                                DayPreviewCard(
                                    surfArea,
                                    "no data",
                                    Pair("", ""),
                                    ConditionStatus.BLANK,
                                    0,
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
                val alert = alerts.first()
                val alertMessage = alert.properties?.description ?: "No description available"
                val awarenessLevel = alert.properties?.awarenessLevel
                val icon = awarenessLevel?.let { getIconBasedOnAwarenessLevel(it) }
                    ?: R.drawable.icon_awareness_default


                CustomAlert(
                    title = surfArea.name,
                    message = alertMessage.toString(),
                    actionText = "OK",
                    warningIcon = icon,
                    data = null,
                    showAlert = remember { mutableStateOf(true) },
                    //actionWithValue = null,
                    action = null,
                )
            }
            ProgressIndicator(isDisplayed = surfAreaScreenUiState.loading)
        }
    }
}


fun getIconBasedOnAwarenessLevel(awarenessLevel: String): Int {
    return try {
        if (awarenessLevel.isNotEmpty()) {
            val firstChar = awarenessLevel.firstOrNull()?.toString()

            when (firstChar) {
                "2" -> R.drawable.icon_awareness_yellow_outlined
                "3" -> R.drawable.icon_awareness_orange
                "4" -> R.drawable.icon_awareness_red
                else -> R.drawable.icon_awareness_default // If awarenessLevel is not 2, 3, or 4
            }
        } else {
            R.drawable.icon_awareness_default // If awarenessLevel is an empty string
        }
    } catch (e: Exception) {
        R.drawable.icon_awareness_default
    }
}


@Composable
fun InfoCard(surfArea: SurfArea) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .width(300.dp)
            .height(350.dp)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = surfArea.locationName,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF313341)
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(surfArea.description),
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF4D5E6F),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Image(
                painter = painterResource(id = surfArea.image),
                contentDescription = "Image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .width(250.dp)
                    .height(150.dp)
            )
        }
    }
}


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
fun HeaderCard(surfArea: SurfArea, icon : String, date: LocalDate) {
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
                                color = Color(0xFF313341),
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
                            style = TextStyle(
                                fontSize = 13.sp,
                                //  fontFamily = FontFamily(Font(R.font.inter)),
                                fontWeight = FontWeight(400),
                                color = Color(0xFF9A938C),
                            ),
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
    dayIndex: Int,
    navController: NavController?
) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .width(93.dp)
            .height(120.dp)
            .background(color = SchemesSurface, shape = RoundedCornerShape(size = 20.dp))
            .clickable(
                onClick = { navController?.navigate("DailySurfAreaScreen/${surfArea.locationName}/$dayIndex") ?: Unit }
            )
            .shadow(4.dp, shape = RoundedCornerShape(10.dp))
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
                    text = day,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF9A938C),

                        ),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(5.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text (
                    text = conditionStatus?.description ?: ""
                )
//                Image(
//                    painter = painterResource(id = R.drawable.surfboard_5525217),
//                    contentDescription = "image description",
//                    contentScale = ContentScale.FillBounds,
//                    modifier = Modifier
//                        .width(40.dp)
//                        .height(40.dp)
//                    //.padding(5.dp)
//                )
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
                Column {
                    Text(
                        text = "${waveHeightMinMax.first} - ${waveHeightMinMax.second}"
                    )
                }
            }
        }
    }
}





@Preview(showBackground = true)
@Composable
private fun PreviewSurfAreaScreen() {
    MyApplicationTheme {
        SurfAreaScreen("Solastranden")
        //DayPreviewCard()
        //HeaderCard()
        //InfoCard()
    }
}