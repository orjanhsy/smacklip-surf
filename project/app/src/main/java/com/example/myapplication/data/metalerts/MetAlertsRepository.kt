package com.example.myapplication.data.metalerts


import androidx.compose.ui.geometry.CornerRadius
import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.metalerts.Features


interface MetAlertsRepository{
    suspend fun getFeatures(): List<Features>
    fun getRelevantAlertsFor(surfArea: SurfArea, allFeatures: List<Features>): List<Features>

}
class MetAlertsRepositoryImpl (

    private val metAlertsDataSource : MetAlertsDataSource = MetAlertsDataSource()

) : MetAlertsRepository {

    private var allFeatures: List<Features> = listOf()
    override suspend fun getFeatures(): List<Features> {
        allFeatures = metAlertsDataSource.fetchMetAlertsData().features
        return allFeatures
    }


    private fun inArea(lat: Double, lon: Double, surfArea: SurfArea, radius: Double = 0.1): Boolean {
        return (
            lat in surfArea.lat - radius..surfArea.lat + radius &&
            lon in surfArea.lon - radius..surfArea.lon + radius
        )
    }

    override fun getRelevantAlertsFor(surfArea: SurfArea, allFeatures: List<Features>): List<Features> {
        val relevantAlerts: MutableList<Features> = mutableListOf()
        allFeatures.forEach() {feature ->
            val coordinates = feature.geometry?.coordinates
            if (feature.geometry?.type == "Polygon") {
                coordinates?.forEach {i ->
                    i.forEach { j ->
                        val lon = j[0] as Double
                        val lat = j[1] as Double
                        if (inArea(lat, lon, surfArea)) {
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
                            if (inArea(lat, lon, surfArea)) {
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