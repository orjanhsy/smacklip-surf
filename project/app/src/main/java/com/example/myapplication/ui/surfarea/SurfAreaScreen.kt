package com.example.myapplication.ui.surfarea

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.myapplication.R
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.commonComponents.BottomBar
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SchemesSurface
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@Composable
fun SurfAreaScreen(
    surfAreaName: String,
    surfAreaScreenViewModel: SurfAreaScreenViewModel = viewModel(),
    onNavigateToDailySurfAreaScreen: (String) -> Unit
) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!

    val surfAreaScreenUiState: SurfAreaScreenUiState by surfAreaScreenViewModel.surfAreaScreenUiState.collectAsState()
    val nextSevenDays = surfAreaScreenUiState.forecast7Days
    surfAreaScreenViewModel.updateForecastNext7Days(surfArea)

    val formatter = DateTimeFormatter.ofPattern("EEE", Locale("no", "NO"))
    val navController = NavigationManager.navController
    Scaffold(
        bottomBar = {
            BottomBar(
                onNavigateToMapScreen = {
                    navController?.navigate("MapScreen")
                    //navigerer til mapscreen
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                //.padding(8.dp),
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),

        verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderCard(surfArea)
            LazyRow(
                modifier = Modifier.padding(5.dp)
            ) {
                if (nextSevenDays.isNotEmpty()) {
                    val today = LocalDate.now()

                    items(nextSevenDays.size) { dayIndex ->
                        val date = today.plusDays(dayIndex.toLong())
                        val formattedDate = formatter.format(date)

                        val surfAreaDataForDay = nextSevenDays.getOrElse(dayIndex) { emptyList() }
                        var maxWaveHeight = 0.0
                        surfAreaDataForDay.forEach { surfAreaDataForHour ->
                            if (maxWaveHeight < surfAreaDataForHour.second[0]) {
                                maxWaveHeight = surfAreaDataForHour.second[0]
                            }
                        }

                        val maxWaveHeightperDay = maxWaveHeight
                        DayPreviewCard(
                            surfArea,
                            formattedDate,
                            maxWaveHeightperDay.toString()
                        ) {}
                    }
                } else {
                    items(6) { dayIndex ->
                        DayPreviewCard(surfArea, "no data", "no data") {}
                    }
                }
            }
            InfoCard(surfArea)
        }
    }
}

@Composable
fun ArrowBackHeader(){
        Image(
            painter = painterResource(id = R.drawable.reply), //trenger mer i Next7days i smacklip for å hente
            contentDescription = "image description",
            //contentScale = ContentScale.None,
            modifier = Modifier
                .width(60.dp)
                .height(60.dp)
        )
    }


@Composable
fun InfoCard(surfArea: SurfArea) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .height(300.dp)
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
fun HeaderCard(surfArea: SurfArea) {

    val currentDate = LocalDate.now()
    val formatter1 = DateTimeFormatter.ofPattern("E d. MMM", Locale("no", "NO"))

    val formattedDate1 = formatter1.format(currentDate)
    println("$formattedDate1")
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
            Row {
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.reply), //trenger mer i Next7days i smacklip for å hente
                        contentDescription = "image description",
                        modifier = Modifier
                            .width(50.dp)
                            .height(50.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(
                        0.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {

                    Column(
                        modifier = Modifier
                            .padding(horizontal = 18.dp)
                            .padding(top = 16.dp),

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
                                    .width(145.dp)
                                    .height(72.dp)
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
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
                            painter = painterResource(id = R.drawable.cludy), //trenger mer i Next7days i smacklip for å hente
                            contentDescription = "image description",
                            //contentScale = ContentScale.None,
                            modifier = Modifier
                                .width(126.dp)
                                .height(126.dp)
                        )
                    }
                }
            }
        }
    }
}


    @Composable
    fun DayPreviewCard(
        surfArea: SurfArea,
        day: String,
        waveheight: String,
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
                    Image(
                        painter = painterResource(id = R.drawable.surfboard_5525217),
                        contentDescription = "image description",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .width(40.dp)
                            .height(40.dp)
                        //.padding(5.dp)
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
                    Column {
                        Text(
                            text = "$waveheight"
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