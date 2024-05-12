package com.example.myapplication.data.metalerts


import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.metalerts.MetAlerts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


interface MetAlertsRepository{
    suspend fun loadAllRelevantAlerts(): Map<SurfArea, List<Alert>>

    val alerts: StateFlow<Map<SurfArea, List<Alert>>>
}

const val ALERT_RADIUS = 50.0 // 10 == 1 mil, 20 er sikkert nice men 50 er nice for testing
class MetAlertsRepositoryImpl (

    private val metAlertsDataSource : MetAlertsDataSource = MetAlertsDataSource()

) : MetAlertsRepository {

    private val _alerts: MutableStateFlow<Map<SurfArea, List<Alert>>> = MutableStateFlow(mapOf())
    override val alerts: StateFlow<Map<SurfArea, List<Alert>>> = _alerts.asStateFlow()

    private suspend fun loadAlerts() {
        _allAlerts = try {
             metAlertsDataSource.fetchMetAlertsData().features
        } catch (e: Exception) {
            emptyList()
        }
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

    override suspend fun loadAllRelevantAlerts(): Map<SurfArea, List<Alert>> {
        if (_allAlerts.isEmpty()) {
            loadAlerts()
        }
        return SurfArea.entries.associateWith { getRelevantAlertsFor(it) }
    }

    private fun getRelevantAlertsFor(surfArea: SurfArea): List<Alert> {
        val relevantAlerts: MutableList<Alert> = mutableListOf()
        _allAlerts.forEach {alert ->
            val coordinates = alert.geometry?.coordinates
            if (alert.geometry?.type == "Polygon") {
                coordinates?.forEach {i ->
                    i.forEach { j ->
                        val lon = j[0] as Double
                        val lat = j[1] as Double
                        if (distanceTo(lat, lon, surfArea) < ALERT_RADIUS) { // henter alle varsel innenfor en mil
                            alert.let { relevantAlerts.add(it) }
                        }
                    }
                }
            } else if (alert.geometry?.type == "MultiPolygon") {
                coordinates?.forEach { i ->
                    i.forEach { j ->
                        j.forEach { k ->
                            val coords = k as List<*>
                            val lon = coords[0] as Double
                            val lat = coords[1] as Double
                            if (distanceTo(lat, lon, surfArea) < ALERT_RADIUS) { // henter alle varsel innenfor en mil
                                alert.let { relevantAlerts.add(it) }
                            }
                        }
                    }
                }
            }
        }
        return relevantAlerts
    }

}