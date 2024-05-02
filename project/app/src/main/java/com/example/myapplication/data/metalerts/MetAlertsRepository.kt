package com.example.myapplication.data.metalerts


import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.metalerts.Alert
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


interface MetAlertsRepository{
    suspend fun getFeatures(): List<Alert>
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Alert>

}
class MetAlertsRepositoryImpl (

    private val metAlertsDataSource : MetAlertsDataSource = MetAlertsDataSource()

) : MetAlertsRepository {

    private var allFeatures: List<Alert> = listOf()
    override suspend fun getFeatures(): List<Alert> {
        allFeatures = metAlertsDataSource.fetchMetAlertsData().features
        return allFeatures
    }


    private fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double {
        // acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon2-lon1))*6371 (6371 is Earth radius in km.)
        val radiusEarth = 6371
        val lat1 = surfArea.lat * PI / 180
        val lon1 = surfArea.lon * PI / 180
        val lat2 = lat * PI / 180
        val lon2 = lon * PI / 180
        return acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2-lon1)) * radiusEarth
    }

    override suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Alert> {
        val relevantAlerts: MutableList<Alert> = mutableListOf()
        if (allFeatures.isEmpty()) {
            getFeatures()
        }
        allFeatures.forEach() {feature ->
            val coordinates = feature.geometry?.coordinates
            if (feature.geometry?.type == "Polygon") {
                coordinates?.forEach {i ->
                    i.forEach { j ->
                        val lon = j[0] as Double
                        val lat = j[1] as Double
                        if (distanceTo(lat, lon, surfArea) < 10.0) { // henter alle varsel innenfor en mil
                            feature.let { relevantAlerts.add(it) }
                        }
                    }
                }
            } else if (feature.geometry?.type == "MultiPolygon") {
                coordinates?.forEach { i ->
                    i.forEach { j ->
                        j.forEach { k ->
                            val coords = k as List<*>
                            val lon = coords[0] as Double
                            val lat = coords[1] as Double
                            if (distanceTo(lat, lon, surfArea) < 10.0) { // henter alle varsel innenfor en mil
                                feature.let { relevantAlerts.add(it) }
                            }
                        }
                    }
                }
            }
        }
        return relevantAlerts
    }

}