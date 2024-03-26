package com.example.myapplication.data.map

import com.example.myapplication.model.SurfArea
import com.mapbox.geojson.Point

interface mapRepository {
    fun locationToPoint(): List<Point>
}

class mapRepositoryImpl {

    fun locationToPoint(): List<Point> {
        return SurfArea.entries.map {
            Point.fromLngLat(it.lon, it.lat)
        }
    }

}