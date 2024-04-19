
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
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CallMade
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import com.example.myapplication.ui.commonComponents.BottomBar
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.surfarea.HeaderCard
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun DailySurfAreaScreen(surfAreaName: String, dailySurfAreaScreenViewModel: DailySurfAreaScreenViewModel = viewModel()) {

    val surfArea: SurfArea = SurfArea.entries.find {
        it.locationName == surfAreaName
    }!!

    val dailySurfAreaScreenUiState by dailySurfAreaScreenViewModel.dailySurfAreaScreenUiState.collectAsState()
    val nextSevenDays = dailySurfAreaScreenUiState.forecast7Days
    dailySurfAreaScreenViewModel.updateForecastNext7Days(surfArea = surfArea)

    Scaffold(
        bottomBar = {
            BottomBar()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeaderCard(surfArea)
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .padding(innerPadding)
            ) {//vent dette er feil, dette er jo bare for i dag, må fikses med onclick
                val surfAreaDataForDay =
                    nextSevenDays.getOrElse(0) { emptyList() } //0 er altså i dag
                if (surfAreaDataForDay.isNotEmpty()) {
                    items(surfAreaDataForDay.size) { hourIndex -> //altså timer igjen av dagen
                        val surfAreaDataForHour =
                            surfAreaDataForDay[hourIndex] //henter objektet for timen som er en liste med Pair<List<Int>, Double>
                        val timestamp = surfAreaDataForHour.first[3] //3??
                        val waveHeight = surfAreaDataForHour.second[0]
                        val waveDir = surfAreaDataForHour.second[1]
                        val windDir = surfAreaDataForHour.second[2]
                        val windSpeed = surfAreaDataForHour.second[3]
                        val windGust = surfAreaDataForHour.second[4]

                        Log.d("timestamp", "$timestamp")
                        AllInfoCard(
                            timestamp = timestamp.toString(),
                            surfArea = surfArea,
                            waveHeight = waveHeight,
                            windSpeed = windSpeed,
                            windGust = windGust,
                            windDir = windDir,
                            waveDir = waveDir

                        )
                    }
                } else {
                    item {
                        AllInfoCard(
                            timestamp = "nei",
                            surfArea = surfArea,
                            waveHeight = 0.0,
                            windSpeed = 0.0,
                            windGust = 0.0,
                            windDir = 0.0,
                            waveDir = 0.0
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
    waveHeight: Double,
    windSpeed: Double,
    windGust: Double,
    windDir: Double,
    waveDir: Double,
) {
    Card(
        modifier = Modifier
            .padding(3.dp)
            .width(340.dp)
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
                text = "2 sek",
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
                text = "18",
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight(400),
                    color = Color(0xFF9C9EAA),
                    textAlign = TextAlign.Center,
                ),
                modifier = Modifier.padding(4.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.ellipse14),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .aspectRatio(1f)
                    .shadow(
                        elevation = 2.5.dp,
                        spotColor = Color(0x33FBCA1C),
                        ambientColor = Color(0x33FBCA1C)
                    )
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
