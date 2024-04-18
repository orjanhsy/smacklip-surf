
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.myapplication.ui.surfarea.DailySurfAreaScreenViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailySurfAreaScreen(dailySurfAreaScreenViewModel: DailySurfAreaScreenViewModel = viewModel()) {
    val dailySurfAreaScreenUiState by dailySurfAreaScreenViewModel.dailySurfAreaScreenUiState.collectAsState()
    val nextSevenDays = dailySurfAreaScreenUiState.forecast7Days
    dailySurfAreaScreenViewModel.updateForecastNext7Days(SurfArea.HODDEVIK)


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        val surfAreaDataForDay = nextSevenDays.getOrElse(0) { emptyList() } //0 er altså i dag
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
                    waveHeight = waveHeight,
                    windSpeed = windSpeed,
                    windGust = windGust
                )
            }
        } else {
            item {
                AllInfoCard(
                    timestamp = "nei",
                    waveHeight = 0.0,
                    windSpeed = 0.0,
                    windGust = 0.0
                )
            }
        }
    }
}

@Composable
fun AllInfoCard(
    timestamp : String,
    waveHeight: Double,
    windSpeed: Double,
    windGust: Double,
) {
    Card(
        modifier = Modifier
            .padding(3.dp)
            .width(331.dp)
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

            Image(
                painter = painterResource(id = R.drawable.air),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .aspectRatio(1f)
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

            Image(
                painter = painterResource(id = R.drawable.tsunami),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .aspectRatio(1f)
            )

            Text(
                text = "$waveHeight",
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

            Image(
                painter = painterResource(id = R.drawable.call_made),
                contentDescription = "image description",
                contentScale = ContentScale.None,
                modifier = Modifier
                    .padding(4.dp)
                    .size(24.dp)
                    .aspectRatio(1f)
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
private fun PreviewDailyScreen() {
    MyApplicationTheme {
        DailySurfAreaScreen()
    }
}
