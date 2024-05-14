package no.uio.ifi.in2000.team8.data.metalerts


import android.util.Log
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.model.metalerts.Alert
import no.uio.ifi.in2000.team8.model.metalerts.MetAlerts
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

private const val TAG = "AlertRepo"

interface MetAlertsRepository{
    suspend fun loadAllRelevantAlerts()

    val alerts: StateFlow<Map<SurfArea, List<Alert>>>
}

const val ALERT_RADIUS = 50.0 // km
class MetAlertsRepositoryImpl (

    private val metAlertsDataSource : MetAlertsDataSource = MetAlertsDataSource()

) : MetAlertsRepository {


    // holds relevant alerts
    private val _alerts: MutableStateFlow<Map<SurfArea, List<Alert>>> = MutableStateFlow(mapOf())
    override val alerts: StateFlow<Map<SurfArea, List<Alert>>> = _alerts.asStateFlow()

    private suspend fun getAlerts(): List<Alert> {
        return try {
            metAlertsDataSource.fetchMetAlertsData().features
        } catch (e: ClientRequestException) {
            val statusCode = e.response.status.value
            val message = e.response.bodyAsText()
            Log.e(TAG, "Client side error occurred while loading alerts. Status code: $statusCode, Message: $message")

            emptyList()
        } catch (e: ServerResponseException) {
            val statusCode = e.response.status.value
            val message = e.response.bodyAsText()
            Log.e(TAG, "Server side error occurred while loading alerts. Status code: $statusCode, Message: $message")

            emptyList()
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred while loading alerts: $e")

            emptyList()
        }
    }


    // measures distance from a point to a surfArea (point)
    private fun distanceTo(lat: Double, lon: Double, surfArea: SurfArea): Double {
        val radiusEarth = 6371
        val lat1 = surfArea.lat * PI / 180
        val lon1 = surfArea.lon * PI / 180
        val lat2 = lat * PI / 180
        val lon2 = lon * PI / 180
        return acos(sin(lat1) * sin(lat2) + cos(lat1) * cos(lat2) * cos(lon2-lon1)) * radiusEarth
    }

    override suspend fun loadAllRelevantAlerts() {

        _alerts.update {
            val allAlerts = getAlerts()
            SurfArea.entries.associateWith { getRelevantAlertsFor(it, allAlerts) }
        }
    }

    // retrieves all alerts for an area that are within ALERT_RADIUS distance
    private fun getRelevantAlertsFor(surfArea: SurfArea, allAlerts: List<Alert>): List<Alert> {
        val relevantAlerts: MutableList<Alert> = mutableListOf()
        allAlerts.forEach {alert ->
            val coordinates = alert.geometry?.coordinates
            if (alert.geometry?.type == "Polygon") {
                coordinates?.forEach {i ->
                    i.forEach { j ->
                        val lon = j[0] as Double
                        val lat = j[1] as Double
                        if (distanceTo(lat, lon, surfArea) < ALERT_RADIUS) {
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
                            if (distanceTo(lat, lon, surfArea) < ALERT_RADIUS) {
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