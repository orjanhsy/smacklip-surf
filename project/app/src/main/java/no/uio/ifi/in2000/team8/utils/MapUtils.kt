package com.example.myapplication.utils

import com.example.myapplication.model.surfareas.SurfArea
import com.mapbox.geojson.Point

class MapUtils {

    //get a list mapping surf areas to the respective point based on the coorrdinates
    fun locationToPoint(): List<Pair<SurfArea, Point>> {
        return SurfArea.entries.map {
            Pair(it, Point.fromLngLat(it.lon, it.lat))
        }
    }

    //Helper method to check if two coordinates are approximately equal using the value of threshold.
    fun isMatchingCoordinates(point1: Point, point2: Point): Boolean {
        val threshold = 0.001
        return kotlin.math.abs(point1.latitude() - point2.latitude()) <= threshold &&
                kotlin.math.abs(point1.longitude() - point2.longitude()) <= threshold
    }
}