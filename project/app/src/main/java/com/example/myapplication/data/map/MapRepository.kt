package com.example.myapplication.data.map

import com.example.myapplication.model.surfareas.SurfArea
import com.mapbox.geojson.Point


interface MapRepository {
    fun locationToPoint(): List<Pair<SurfArea, Point>>
}

class MapRepositoryImpl : MapRepository{

    override fun locationToPoint(): List<Pair<SurfArea, Point>> {
        return SurfArea.entries.map {
            Pair(it, Point.fromLngLat(it.lon, it.lat))
        }
    }

}
