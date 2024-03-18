package com.example.myapplication.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.SurfArea
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeScreenViewModel : HomeScreenViewModel = viewModel()) {

    val homeScreenUiState : HomeScreenUiState by homeScreenViewModel.homeScreenUiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(text = "Locations")
                })
        }) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(SurfArea.entries) { location ->
                SurfAreaCard(location, homeScreenUiState.windSpeed, homeScreenUiState.waveHeight)
            }
        }
    }
}



@Composable
fun SurfAreaCard(
    surfArea : SurfArea,
    windSpeed : List<Pair<String, Double>>,
    waveHeight : List<Pair<String, Double>>
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            //contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {


            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Row {
                    Text(
                        text = surfArea.locationName,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row {
                    Text(
                        text = "Wind: ${if (windSpeed.isNotEmpty()) windSpeed[0].second else ""}"
                    )
                }

                Row {
                    Text(
                        text = "Wave height: ${if(waveHeight.isNotEmpty()) waveHeight[0].second else ""}"
                    )
                }
            }
            Column (
                horizontalAlignment = Alignment.End
            ){
                if (surfArea.image != 0){
                    Image(painter = painterResource(id = surfArea.image), 
                        contentDescription = null

                        )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSurfAreaCard() {
    MyApplicationTheme {
        SurfAreaCard(SurfArea.HODDEVIK, listOf(Pair("time", 1.0)), listOf(Pair("time", 5.0)))
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    MyApplicationTheme {
        HomeScreen()
    }
}


