package com.example.myapplication.ui.map

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import com.example.myapplication.R
import com.example.myapplication.model.SurfArea
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
fun MapScreen() {
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
                    .fillMaxSize()
            )
        }
    }

}
@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
) {
    val trondheim = Point.fromLngLat(10.4, 63.4)
    val context = LocalContext.current

    val marker = remember(context) {
        context.getDrawable(R.drawable.red_marker )!!.toBitmap()
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
                    //it.deleteAll()
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(trondheim)
                        .withIconImage(marker)

                    it.create(pointAnnotationOptions)
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
    MapScreen()
}