package com.example.myapplication.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.NavigationManager
import com.example.myapplication.R
import com.example.myapplication.data.map.MapRepositoryImpl
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.commonComponents.BottomBar
import com.example.myapplication.ui.home.HomeScreenViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import org.slf4j.Marker


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(mapScreenViewModel : MapScreenViewModel = viewModel()) {

    val mapScreenUiState : MapScreenUiState by mapScreenViewModel.mapScreenUiState.collectAsState()
    val mapRepository : MapRepositoryImpl = MapRepositoryImpl() //bruker direkte maprepository fordi mapbox har sin egen viewmodel? -
    val navController = NavigationManager.navController
    // TODO: sjekke (maprepository) ut at dette er ok.
    

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
        },
        bottomBar = {
            BottomBar(
                onNavigateToHomeScreen = {
                    navController?.navigate("HomeScreen")
                    // Navigerer til HomeScreen
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MapBoxMap(
                modifier = Modifier
                    .fillMaxSize(),
                locations = mapRepository.locationToPoint(),
                uiState = mapScreenUiState

            )
        }
    }

}


@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    locations: List<Pair<SurfArea, Point>>,
    uiState: MapScreenUiState
) {
    val trondheim = Point.fromLngLat(10.4, 63.4) //trondheim kommer i senter av skjermen, kan endre koordinater så hele norge synes?
    val context = LocalContext.current
    val marker = remember(context) {
        context.getDrawable(R.drawable.marker )!!.toBitmap()
    }

    val selectedMarker = remember { mutableStateOf<SurfArea?>(null) }


    var pointAnnotationManager: PointAnnotationManager? by remember {
        mutableStateOf(null)
    }

    //her vises selve kartet
    Box (
        contentAlignment = Alignment.Center
    ){
        AndroidView(
            factory = {
                MapView(it).also { mapView ->
                    mapView.mapboxMap.loadStyle(Style.STANDARD)
                    val annotationApi = mapView.annotations
                    pointAnnotationManager = annotationApi.createPointAnnotationManager()
                    //avgjør hvordan kartet skal vises når de først lastes inn:
                    mapView.mapboxMap
                        .flyTo(CameraOptions.Builder().zoom(4.0).center(trondheim).build())
                }
            },
            update = { mapView ->

                pointAnnotationManager?.let {



                    //it.deleteAll() //fjerner alle tidligere markører hvis kartet oppdateres for å forhindre duplikater/uønskede markører

                    it.addClickListener { pointAnnotation ->
                        // Handle the click event, e.g., showing a Toast or navigating to another screen
                        val clickedPoint = pointAnnotation.point
                        Log.d(
                            "pointAnnotation point: ",
                            clickedPoint.toString() + " " + clickedPoint.longitude() + " " + clickedPoint.latitude()
                        )

                        try {
                            val loc = locations.first { location ->
                                isMatchingCoordinates(
                                    location.second,
                                    clickedPoint
                                )
                            }
                            selectedMarker.value = loc.first

                        } catch (_: NoSuchElementException) { //first-metode utløser unntak og appen krasjer dersom den ikke finner like koordinater
                            selectedMarker.value = null
                        }
                        true // Return true to indicate that the click event has been handled
                    }

                    //legger til markers for hvert sted
                    locations.forEach { (location, point) ->
                        Log.d("PointsList", location.locationName + " " + point.toString())

                        val pointAnnotationOptions = PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(marker)
                        it.create(pointAnnotationOptions)

                    }
                }

                NoOpUpdate
            },
            modifier = modifier
        )
        if (selectedMarker.value != null) {
            SurfAreaCard(surfArea = selectedMarker.value!!,
                onCloseClick = {selectedMarker.value = null},
                uiState = uiState
                )
        }
    }

}


//hjelpemetode for å sjekke at to koordinater er tilnærmet like ved bruk av verdien threshold
fun isMatchingCoordinates(point1: Point, point2: Point): Boolean {
    val threshold = 0.1
    return kotlin.math.abs(point1.latitude() - point2.latitude()) <= threshold &&
            kotlin.math.abs(point1.longitude() - point2.longitude()) <= threshold
}


@Composable
fun SurfAreaCard(
    surfArea: SurfArea,
    onCloseClick: () -> Unit,
    uiState: MapScreenUiState
    ){

    //current data for surfArea som sendes inn:
    val windSpeed: Double = uiState.windSpeed[surfArea]?.get(0)?.second ?: 0.0
    val windGust: Double = uiState.windGust[surfArea]?.get(0)?.second ?: 0.0
    val windDirection: Double = uiState.windDirection[surfArea]?.get(0)?.second ?: 0.0
    val airTemperature: Double = uiState.airTemperature[surfArea]?.get(0)?.second ?: 0.0
    val symbolCode: String = uiState.symbolCode[surfArea]?.get(0)?.second ?: ""
    val waveHeight: Double = uiState.waveHeight[surfArea]?.get(0)?.second ?: 0.0

    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ){
                Button(onClick = onCloseClick,
                ) {
                    Text("X")
                }
            }

            //Overskrift: navn på stedet
            Text(text = surfArea.locationName,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            //tekstlig beskrivelse av stedet
            Text(text = surfArea.description,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            //info om vind, bølger og temperatur
            Row (
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(painter = painterResource(id = R.drawable.air),
                    contentDescription = "Air icon",
                    modifier = Modifier
                        .padding(8.dp)
                        .width(18.dp)
                        .height(18.dp))
                Text(text = "$windSpeed($windGust)", modifier = Modifier.padding(8.dp))
                Image(
                    painter = painterResource(id = R.drawable.tsunami),
                    contentDescription = "wave icon",
                    modifier = Modifier
                        .padding(8.dp)
                        .width(18.dp)
                        .height(18.dp),

                )
                Text(text = "$waveHeight", modifier = Modifier.padding(8.dp))
                val symbolCodePng :String = "$airTemperature.png"
                Image(
                    painter = painterResource(id = R.drawable.fair_day),
                    contentDescription = "wave icon",
                    modifier = Modifier
                        .padding(8.dp)
                        .width(30.dp)
                        .height(30.dp)

                )
                Text(text = "$airTemperature °C", modifier = Modifier.padding(8.dp))
                Text(text = symbolCode, modifier = Modifier.padding(8.dp))
            }

            if (surfArea.image != 0) {
                Image(
                    painter = painterResource(id = surfArea.image),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)

                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ){
                Button(onClick = onCloseClick, //TODO: må byttes ut med navigation
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Gå til "+surfArea.locationName)
                }
            }

        }
    }
}

@Composable
fun SurfAreaCard2(
    surfArea: SurfArea,
    windSpeedMap: Map<SurfArea, List<Pair<List<Int>, Double>>>,
    windGustMap: Map<SurfArea, List<Pair<List<Int>, Double>>>,
    windDirectionMap: Map<SurfArea, List<Pair<List<Int>, Double>>>,
    waveHeightMap: Map<SurfArea,List<Pair<List<Int>, Double>>>,

    alerts: List<Features>?,
    homeScreenViewModel: HomeScreenViewModel,
    showFavoriteButton: Boolean = true,
    onNavigateToSurfAreaScreen: (String) -> Unit
) {

    val windSpeed = windSpeedMap[surfArea] ?: listOf()
    val windGust = windGustMap[surfArea] ?: listOf()
    val windDirection = windDirectionMap[surfArea] ?: listOf()
    val waveHeight = waveHeightMap[surfArea] ?: listOf()




    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 8.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
            .clickable(
                onClick = { onNavigateToSurfAreaScreen(surfArea.locationName) })
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .width(162.dp)
                .height(162.dp)

        ) {

            // Stjerneikon
            if (showFavoriteButton) {
                IconButton(
                    onClick = { homeScreenViewModel.updateFavorites(surfArea) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 0.dp, top = 0.dp)
                ){
                    Icon(
                        painter = painterResource(id = homeScreenViewModel.updateFavoritesIcon(surfArea)),
                        contentDescription = "Toggle favorite",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
            )
            {

                Row {

                    Text(
                        text = surfArea.locationName,
                        style = TextStyle(
                            fontSize = 12.93.sp,
                            lineHeight = 19.4.sp,
                            fontWeight = FontWeight(700),
                            letterSpacing = 0.12.sp

                        )

                    )
                }
                Row {
                    Image(
                        painter = painterResource(id = R.drawable.tsunami),
                        contentDescription = "wave icon",
                        modifier = Modifier
                            .padding(0.02021.dp)
                            .width(15.3587.dp)
                            .height(14.47855.dp)

                    )

                    Text(
                        text = " ${if (waveHeight.isNotEmpty()) "${waveHeight[0].second}m" else ""}"
                    )

                }

                Row {
                    Image(
                        painter = painterResource(id = R.drawable.air),
                        contentDescription = "Air icon",
                        modifier = Modifier
                            .padding(0.02021.dp)
                            .width(15.3587.dp)
                            .height(13.6348.dp)
                    )
                    Text(
                        text = " ${if (windSpeed.isNotEmpty()) windSpeed[0].second else ""}" +
                                if(windGust.isNotEmpty() && windSpeed.isNotEmpty() && windGust[0].second != windSpeed[0].second) "(${windGust[0].second})" else ""
                    )
                    Text(
                        text = " ${if (windDirection.isNotEmpty()) "${windDirection[0].second}°" else ""}"
                    )
                }


                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    if (surfArea.image != 0) {
                        Image(
                            painter = painterResource(id = surfArea.image),
                            contentDescription = "SurfArea Image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(162.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))


                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SurfAreaPreview(){
    MyApplicationTheme {
        SurfAreaCard(surfArea = SurfArea.HODDEVIK, {}, MapScreenUiState())
    }
}


@Preview
@Composable
fun MapScreenPreview(){
    MyApplicationTheme {
        MapScreen()
    }

}
