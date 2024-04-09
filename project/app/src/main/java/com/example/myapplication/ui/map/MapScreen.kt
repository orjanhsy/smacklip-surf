package com.example.myapplication.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.data.map.MapRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
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
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            MapBoxMap(
                modifier = Modifier
                    .fillMaxSize(),
                //locations = mapScreenUiState.points
                locations = mapRepository.locationToPoint()

            )
        }
    }

}


@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    locations: List<Pair<SurfArea, Point>>,
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



                    it.deleteAll() //fjerner alle tidligere markører hvis kartet oppdateres for å forhindre duplikater/uønskede markører

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
                            Toast.makeText(
                                context,
                                "Clicked on marker ${loc.first.locationName}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (_: NoSuchElementException) { //first-metode utløser unntak og appen krasjer dersom den ikke finner like koordinater
                            Toast.makeText(context, "Clicked on marker null", Toast.LENGTH_SHORT)
                                .show()
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
            Log.d("if-test surfarea", selectedMarker.value.toString())
            SurfAreaCard(surfArea = selectedMarker.value!!)
        }
    }

}


//hjelpemetode for å sjekke at to koordinater er tilnærmet like ved bruk av verdien threshold
fun isMatchingCoordinates(point1: Point, point2: Point): Boolean {
    Log.d("matching coordinates", "$point1 $point2")
    val threshold = 0.1
    return kotlin.math.abs(point1.latitude() - point2.latitude()) <= threshold &&
            kotlin.math.abs(point1.longitude() - point2.longitude()) <= threshold
}


@Composable
fun SurfAreaCard(surfArea: SurfArea){
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ){
        Column (
            modifier = Modifier
                .fillMaxWidth()
        ) {
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
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center
            ){
                Text(text = "vind(kast)", modifier = Modifier.padding(8.dp))
                Text(text = "bølger", modifier = Modifier.padding(8.dp))
                Text(text = "grader", modifier = Modifier.padding(8.dp))
                Text(text = "forhold", modifier = Modifier.padding(8.dp))
            }

            if (surfArea.image != 0) {
                Image(
                    painter = painterResource(id = surfArea.image),
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp)

                )
            }

        }
    }
}

@Preview
@Composable
fun SurfAreaPreview(){
    MyApplicationTheme {
        SurfAreaCard(surfArea = SurfArea.HODDEVIK)
    }
}


@Preview
@Composable
fun MapScreenPreview(){
    MyApplicationTheme {
        MapScreen()
    }

}
