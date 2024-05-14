package com.example.myapplication.utils

import com.example.myapplication.model.surfareas.SurfArea
import com.mapbox.geojson.Point

class MapUtils {

    fun locationToPoint(): List<Pair<SurfArea, Point>> {
        return SurfArea.entries.map {
            Pair(it, Point.fromLngLat(it.lon, it.lat))
        }
    }
}