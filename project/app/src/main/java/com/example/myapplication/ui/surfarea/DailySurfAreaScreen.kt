
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
    Log.d("hallo", "i luken")

    val nextSevenDays = dailySurfAreaScreenUiState.forecast7Days
    Log.d("size", "${nextSevenDays.size}")
    val waveHeightMap: Map<SurfArea, List<Pair<List<Int>, Double>>> = mapOf(
        SurfArea.HODDEVIK to listOf(Pair(listOf(1, 2, 3, 4), 5.0))
    )
    val windSpeedMap: Map<SurfArea, List<Pair<List<Int>, Double>>> = mapOf(
        SurfArea.HODDEVIK to listOf(Pair(listOf(2, 4, 6, 8), 1.0))
    )
    val windGustMap: Map<SurfArea, List<Pair<List<Int>, Double>>> = mapOf(
        SurfArea.HODDEVIK to listOf(Pair(listOf(3, 5, 8, 32), 3.0))
    )

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
                // List<Int> = tiden
                val timestamp = surfAreaDataForHour.first[0] //3??
                Log.d("timestamp", "$timestamp")
                AllInfoCard(
                    timestamp = timestamp.toString(),
                    surfArea = SurfArea.HODDEVIK,
                    waveHeightMap = waveHeightMap,
                    windSpeedMap = windSpeedMap,
                    windGustMap = windGustMap
                )
            }
        } else {
            item {
                AllInfoCard(
                    timestamp = "nei",
                    surfArea = SurfArea.HODDEVIK,
                    waveHeightMap = waveHeightMap,
                    windSpeedMap = windSpeedMap,
                    windGustMap = windGustMap
                )
            }
        }
    }
}




@Composable
fun AllInfoCard(
    timestamp : String,
    surfArea: SurfArea,
    waveHeightMap: Map<SurfArea,List<Pair<List<Int>, Double>>>,
    windSpeedMap: Map<SurfArea, List<Pair<List<Int>, Double>>>,
    windGustMap: Map<SurfArea, List<Pair<List<Int>, Double>>>,
    ) {

    val waveHeight = waveHeightMap[surfArea] ?: listOf()
    val windSpeed = windSpeedMap[surfArea] ?: listOf()
    val windGust = windGustMap[surfArea] ?: listOf()
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
        )
        {
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
                text = "${if (windSpeed.isNotEmpty()) windSpeed[0].second else ""}" +
                        if(windGust.isNotEmpty() && windSpeed.isNotEmpty() && windGust[0].second != windSpeed[0].second) "(${windGust[0].second})" else "",
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
                text = "${if (waveHeight.isNotEmpty()) waveHeight[0].second else ""}",
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
