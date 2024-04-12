package com.example.myapplication.ui.surfarea

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SurfAreaScreen(surfAreaScreenViewModel: SurfAreaScreenViewModel = viewModel()) {
    val surfAreaScreenUiState: SurfAreaScreenUiState by surfAreaScreenViewModel.surfAreaScreenUiState.collectAsState()
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        items(7) { index ->
            DayPreviewCard()
            }
        }
    }

@Composable
fun InfoCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            //.fillMaxWidth()
        ) {
            Row (
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
                ){
                Text(
                    text = "Hoddevik"
                )
            }
            Row {
                Text(
                    text = "Den en kilometer lange Hoddevikstranda er rangert som en av de ti beste surfe-lokasjonene i verden av den Engelske avisen The Guardian. Få mennesker og de bratte og majestetiske fjellene som omgir stranden er noen av grunnene til at det ble en av de ti beste strendene. Det kommer surfere fra hele verden for å oppleve bølgene i Hoddevik."
                )
            }
        }
    }
}

@Composable
fun HeaderCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        Row {
            Column {
                Text(text = "tmp: Hoddvik")
            }
            Column {
                Text(text = "ikon sol/vær")
            }
        }

    }
}
@Composable
fun DayPreviewCard(){
    Card(
        modifier = Modifier
            .padding(16.dp)
            //.fillMaxWidth()
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
                //.fillMaxWidth()
        ){
            Row{
                Text(
                    text = "mandag"
                )
            }
            Row{
                Text(
                    text = "sol ikon"
                )
            }
            Row{
                Text(
                    text = "bølgehøyde"
                )
            }
        }
    }
}

/*forslag
@Composable
fun ShowForecastNext24hrs(day: String) {
    // vis 0-6, 6-12, 12-18, 18-24

}

@Composable
fun Next24HoursCard() {

}

@Composable
fun ShowForecastNext7Days() {
    val days = listOf<String>("Monday", "Tuesday")
    days.forEach { ShowForecastNext24hrs(day = it) }
}
*/
@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    MyApplicationTheme {
        //SurfAreaScreen()
        //DayPreviewCard()
        //HeaderCard()
        InfoCard()
    }
}