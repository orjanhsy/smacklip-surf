package com.example.myapplication.ui.map

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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


@Composable
fun MapScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        MapBoxMap(
            point = Point.fromLngLat(SurfArea.HODDEVIK.lon, SurfArea.HODDEVIK.lat),
            modifier = Modifier
                .fillMaxSize()
        )
    }
}
@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    point: Point?,
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
            if (point != null) {
                pointAnnotationManager?.let {
                    it.deleteAll()
                    val pointAnnotationOptions = PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(marker)

                    it.create(pointAnnotationOptions)
                    mapView.mapboxMap
                        .flyTo(CameraOptions.Builder().zoom(4.0).center(trondheim).build())
                }
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