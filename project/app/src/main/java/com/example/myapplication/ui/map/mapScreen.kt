package com.example.myapplication.ui.map

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.set
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.R
import com.example.myapplication.model.SurfArea
import com.example.myapplication.ui.home.HomeScreenUiState
import com.example.myapplication.ui.home.HomeScreenViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(mapScreenViewModel : MapScreenViewModel = viewModel()) {

    val mapScreenUiState : MapScreenUiState by mapScreenViewModel.mapScreenUiState.collectAsState()


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
                mapScreenUiState.points
            )
        }
    }

}
@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    points: List<Point>
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
            }
        },
        update = { mapView ->
                pointAnnotationManager?.let {
                    it.deleteAll() //fjerner alle tidligere markører hvis kartet oppdateres for å forhindre duplikater/uønskede markører

                    points.forEach { point ->
                        val pointAnnotationOptions = PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(marker)
                        it.create(pointAnnotationOptions)
                        it.addClickListener { annotation ->
                            // handle marker click
                            Toast.makeText(context, "Marker ${point.latitude()} clicked", Toast.LENGTH_LONG).show()
                            true // return true to consume the click event
                        }
                    }

                    //avgjør hvordan kartet skal vises når de først lastes inn:
                    mapView.mapboxMap
                        .flyTo(CameraOptions.Builder().zoom(4.0).center(trondheim).build())
                }



            NoOpUpdate
        },
        modifier = modifier
    )
}



@Preview
@Composable
fun MapScreenPreview(){
    MyApplicationTheme {
        MapScreen()
    }

}