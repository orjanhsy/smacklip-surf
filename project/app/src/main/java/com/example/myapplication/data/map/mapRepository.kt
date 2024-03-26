package com.example.myapplication.data.map

import com.example.myapplication.model.SurfArea
import com.mapbox.geojson.Point

interface mapRepository {
    fun locationToPoint(): List<Pair<SurfArea, Point>>
}

class mapRepositoryImpl {

    fun locationToPoint(): List<Pair<SurfArea, Point>> {
        return SurfArea.entries.map {
            Pair(it, Point.fromLngLat(it.lon, it.lat))
        }
    }

}