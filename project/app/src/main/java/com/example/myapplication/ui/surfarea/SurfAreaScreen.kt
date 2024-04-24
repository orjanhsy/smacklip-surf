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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.NavigationManager
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.AlertCard.CustomAlert
import com.example.myapplication.ui.commonComponents.BottomBar
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
    onNavigateToDailySurfAreaScreen: (String) -> Unit = {}
) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!



    val surfAreaScreenUiState: SurfAreaScreenUiState by surfAreaScreenViewModel.surfAreaScreenUiState.collectAsState()
    surfAreaScreenViewModel.updateForecastNext7Days(surfArea)
    surfAreaScreenViewModel.updateWavePeriods(surfArea)
    surfAreaScreenViewModel.updateAlertsSurfArea(surfArea)

    val nextSevenDays = surfAreaScreenUiState.forecast7Days
    val alerts = surfAreaScreenUiState.alertsSurfArea

    val formatter = DateTimeFormatter.ofPattern("EEE", Locale("no", "NO"))
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
                },
            )
        },
        bottomBar = {
            BottomBar(
                onNavigateToMapScreen = {
                    navController?.navigate("MapScreen")
                },
                onNavigateToHomeScreen = {
                    navController?.navigate("HomeScreen")
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                //.padding(innerPadding)
               // .padding(horizontal = 16.dp)
        ) {
            // Background surface representing SurfAreaScreen
            Surface(
                modifier = Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),

                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        val surfAreaDataForDay =
                            nextSevenDays.getOrElse(0) { emptyList() } //0 is today
                        val currentHour =
                            LocalTime.now().hour // klokken er 10 så får ikke sjekket om det står 09 eller 9. Sto tidligere "08", "09" med .toString().padStart(2, '0')
                        var headerIcon = ""

                        if (surfAreaDataForDay.isNotEmpty()) {
                            for (surfAreaDataForHour in surfAreaDataForDay) {
                                if (currentHour.toString() == surfAreaDataForHour.first[3].toString()) {
                                    headerIcon = surfAreaDataForHour.second[6].toString()
                                    break
                                }
                            }
                            HeaderCard(surfArea = surfArea, icon = headerIcon)
                        }
                        else{
                            HeaderCard(surfArea = surfArea, icon = "else" )
                        }
                    }
                    item {
                        LazyRow(
                            modifier = Modifier.padding(5.dp)
                        ) {
                            if (surfAreaScreenUiState.forecast7Days.isNotEmpty()) {
                                val today = LocalDate.now()
                                surfAreaScreenViewModel.updateConditionStatuses(
                                    surfArea,
                                    surfAreaScreenUiState.forecast7Days
                                )

                                items(surfAreaScreenUiState.forecast7Days.size) { dayIndex ->
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
                                        Log.d(
                                            "SAscreen",
                                            "ConditionStatus at day $dayIndex was null"
                                        )
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
                                        onNavigateToDailySurfAreaScreen
                                    )
                                }
                            } else {
                                items(6) { dayIndex ->
                                    DayPreviewCard(
                                        surfArea,
                                        "no data",
                                        Pair("", ""),
                                        ConditionStatus.BLANK
                                    ) {}
                                }
                            }
                        }
                    }
                    item {
                        InfoCard(surfArea)
                    }
                }
            }
            if (alerts.isNotEmpty()) {
                val alertMessage = alerts[0].toString()

                CustomAlert(
                    title = surfArea.name,
                    message = alertMessage,
                    actionText = "OK",
                    data = null,
                    showAlert = remember { mutableStateOf(true) },
                    //actionWithValue = null,
                    action = null,
                )
            }
            else{
                CustomAlert(
                    title = "Farevarsel",
                    message = "STORM SØK DEKNING!!!!",
                    actionText = "OK",
                    data = null,
                    showAlert = remember { mutableStateOf(true) },
                    //actionWithValue = null,
                    action = null,
                )
            }
        }
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
                text = surfArea.description,
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
fun HeaderCard(surfArea: SurfArea, icon : String) {

    //getting the right date in the right format
    val currentDate = LocalDate.now()
    val formatter1 = DateTimeFormatter.ofPattern("E d. MMM", Locale("no", "NO"))
    val formattedDate1 = formatter1.format(currentDate)

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
                                fontSize = 30.sp,
                                //fontFamily = FontFamily(Font(R.font.inter)),
                                fontWeight = FontWeight(500),
                                color = Color(0xFF313341),
                            ),
                            modifier = Modifier
                                .padding(16.dp)
                                .width(145.dp)
                                .height(72.dp)
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
    onNavigateToDailySurfAreaScreen: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .width(93.dp)
            .height(120.dp)
            .background(color = SchemesSurface, shape = RoundedCornerShape(size = 20.dp))
            .clickable(
                onClick = { onNavigateToDailySurfAreaScreen(surfArea.locationName) }
            )
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
        SurfAreaScreen("Hoddevik"){}
        //DayPreviewCard()
        //HeaderCard()
        //InfoCard()
    }
}