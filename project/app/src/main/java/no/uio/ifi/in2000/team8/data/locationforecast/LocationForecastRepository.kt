package no.uio.ifi.in2000.team8.data.locationforecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.TimeserieLF

interface LocationForecastRepository {
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>>
}

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
): LocationForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>> {
        val timeSeries : List<TimeserieLF> = try {
            locationForecastDataSource.fetchLocationForecastData(surfArea).properties.timeseries
        } catch (e: Exception) {
            // handles http exceptions thrown by data source
            listOf()
        }
        return timeSeries.map { it.time to it.data }
    }


}
