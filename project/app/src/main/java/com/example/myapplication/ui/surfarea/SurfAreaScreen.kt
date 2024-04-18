package com.example.myapplication.ui.surfarea

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
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
import com.example.myapplication.R
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.SchemesSurface
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HeaderCard(surfArea)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (nextSevenDays.isNotEmpty()) {
                val today = LocalDate.now()

                items(nextSevenDays.size) { dayIndex ->
                    val date = today.plusDays(dayIndex.toLong())
                    val formattedDate = formatter.format(date)

                    //kalkulere bølgehøyde
                    val surfAreaDataForDay = nextSevenDays.getOrElse(dayIndex) { emptyList() }
                    var maxWaveHeight = 0.0
                    surfAreaDataForDay.forEach { surfAreaDataForHour ->
                        if (maxWaveHeight < surfAreaDataForHour.second[0]) {
                            maxWaveHeight = surfAreaDataForHour.second[0]
                        }
                    }

                    //lage kortet
                    val maxWaveHeightperDay = maxWaveHeight

                        DayPreviewCard(
                            surfArea,
                            formattedDate,
                            maxWaveHeightperDay.toString()
                        ) {}
                    }
                }

            else {
                items(6) { dayIndex ->
                    DayPreviewCard(surfArea, "no data", "no data"){}
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        InfoCard(surfArea)
    }
}

@Composable
fun InfoCard(surfArea: SurfArea) {
    Card(
        modifier = Modifier
            .width(336.dp)
            .height(336.dp)
            .padding(16.dp), // Add padding to provide space around the content
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = surfArea.locationName,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF313341)
                ),
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = surfArea.description,
                style = TextStyle(
                    fontSize = 13.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF4D5E6F),
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Image(
                painter = painterResource(id = surfArea.image),
                contentDescription = "image description",
                //contentScale = ContentScale.FillBounds,
                modifier = Modifier.padding(10.dp)
                    .width(312.dp)
                    .height(110.dp)
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderCard(surfArea: SurfArea) {

    val currentDate = LocalDate.now()
    val formatter1 = DateTimeFormatter.ofPattern("E d. MMM",  Locale("no", "NO"))

    val formattedDate1 = formatter1.format(currentDate)
    println("$formattedDate1")



    Card(
        modifier = Modifier
            .shadow(
                elevation = 3.dp,
                spotColor = Color(0x26000000),
                ambientColor = Color(0x26000000)
            )
            .shadow(
                elevation = 2.dp,
                spotColor = Color(0x4D000000),
                ambientColor = Color(0x4D000000)
            )
            .width(317.dp)
            .height(132.dp)
            .background(color = Color(0xFFEFF5F5), shape = RoundedCornerShape(size = 12.dp))
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
        Column {
            Row {
                Text(
                    text = surfArea.locationName, //+surfArea.areaName //hadde vært fint med Stadt
                    style = TextStyle(
                        fontSize = 30.sp,
                        //fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(500),
                        color = Color(0xFF313341),
                    ),
                    modifier = Modifier.padding(16.dp)
                        .width(145.dp)
                        .height(72.dp)
                )
            }
            Row {
                Text(
                    text = formattedDate1,
                    style = TextStyle(
                        fontSize = 13.sp,
                        //  fontFamily = FontFamily(Font(R.font.inter)),
                        fontWeight = FontWeight(400),
                        color = Color(0xFF9A938C),
                    ),
                    modifier = Modifier.padding(5.dp)
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
                    contentScale = ContentScale.None,
                    modifier = Modifier
                        .width(126.dp)
                        .height(126.dp)
                )
            }
        }
    }
}
@Composable
fun DayPreviewCard(surfArea: SurfArea, day: String, waveheight: String, onNavigateToDailySurfAreaScreen: (String) -> Unit) {
    Card(
        modifier = Modifier
            .padding(6.dp)
            .width(93.dp)
            .height(147.dp)
            .background(color = SchemesSurface, shape = RoundedCornerShape(size = 20.dp))
            .clickable(
                onClick = { onNavigateToDailySurfAreaScreen(surfArea.locationName) }
            )
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ){
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day,
                    style = TextStyle(
                        fontSize = 9.sp,
                        fontWeight = FontWeight(400),
                        color = Color(0xFF9A938C),
                    ),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Image(
                    painter = painterResource(id = R.drawable.surfboard_5525217),
                    contentDescription = "image description",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(40.dp)
                        .height(40.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Column {
                    Box(
                        modifier = Modifier
                            .padding(0.03158.dp)
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.tmpwave),
                            contentDescription = "image description",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(40.dp)
                                .height(40.dp)
                        )

                    }
                }
                Column{
                    Text(
                        text = "$waveheight"
                    )
                }
            }
        }
    }
}



@RequiresApi(Build.VERSION_CODES.O)
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