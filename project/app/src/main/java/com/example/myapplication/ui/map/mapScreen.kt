package com.example.myapplication.ui.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.model.SurfArea
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(mapScreenViewModel : MapScreenViewModel = viewModel()) {

    val mapScreenUiState : MapScreenUiState by mapScreenViewModel.mapScreenUiState.collectAsState()
    var location: SurfArea? by remember {
        mutableStateOf(null)
    }

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
                //locations = mapScreenUiState.points,
                locations = SurfArea.entries.map {
                    Pair(it, Point.fromLngLat(it.lon, it.lat))
                },
                onPointChange = { location = it }
            )
        }
    }

}


@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    onPointChange: (SurfArea) -> Unit,
    locations: List<Pair<SurfArea, Point>>
) {
    val trondheim = Point.fromLngLat(10.4, 63.4) //trondheim kommer i senter av skjermen, kan endre koordinater så hele norge synes?
    val context = LocalContext.current
    val marker = remember(context) {
        context.getDrawable(R.drawable.marker )!!.toBitmap()
    }

    var pointAnnotationManager: PointAnnotationManager? by remember {
        mutableStateOf(null)
    }



    AndroidView(
        factory = {
            MapView(it).also { mapView ->
                mapView.mapboxMap.loadStyle(Style.STANDARD)
                val annotationApi = mapView.annotations
                pointAnnotationManager = annotationApi.createPointAnnotationManager()
                var pointAnnotation = pointAnnotationManager!!.annotations
                mapView.mapboxMap
                    .flyTo(CameraOptions.Builder().zoom(4.0).center(trondheim).build())
            }
        },
        update = { mapView ->

            pointAnnotationManager?.let {

                //avgjør hvordan kartet skal vises når de først lastes inn:
                mapView.mapboxMap
                    .flyTo(CameraOptions.Builder().zoom(4.0).center(trondheim).build())

                it.deleteAll() //fjerner alle tidligere markører hvis kartet oppdateres for å forhindre duplikater/uønskede markører

                it.addClickListener { pointAnnotation ->
                    // Handle the click event, e.g., showing a Toast or navigating to another screen
                    val p = pointAnnotation.point
                    Log.d("pointAnnotation point: ", p.toString() +" "+ p.longitude() +" " +p.latitude())
                    try {
                        val loc = locations.first { location -> isMatchingCoordinates(location.second, p) }
                        Toast.makeText(context, "Clicked on marker ${loc.first.locationName}", Toast.LENGTH_SHORT).show()

                    }catch (_: NoSuchElementException){
                        Toast.makeText(context, "Clicked on marker null", Toast.LENGTH_SHORT).show()

                    }

                    true // Return true to indicate that the click event has been handled
                }

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
}


fun isMatchingCoordinates(point1: Point, point2: Point): Boolean {
    Log.d("matching coordinates", "$point1 $point2")
    val threshold = 0.1
    // Check if the clicked point is within the threshold distance of the location
    return kotlin.math.abs(point1.latitude() - point2.latitude()) <= threshold &&
            kotlin.math.abs(point1.longitude() - point2.longitude()) <= threshold
}


fun getLocationAtPoint(points : List<Pair<SurfArea, Point>>, point: Point): SurfArea {
    val res = points.first { it ->
        it.second == point
    }
    return res.first
}

@OptIn(MapboxExperimental::class)
@Composable
fun addPointer(marker: Bitmap, location: String, point: Point, context: Context){

    PointAnnotation(
        iconImageBitmap = marker,
        point = point,
        onClick = {
            Toast.makeText(
                context,
                "Clicked on : $location.",
                Toast.LENGTH_SHORT
            ).show()
            true
        }
    )
}



@Preview
@Composable
fun MapScreenPreview(){
    MyApplicationTheme {
        MapScreen()
    }

}