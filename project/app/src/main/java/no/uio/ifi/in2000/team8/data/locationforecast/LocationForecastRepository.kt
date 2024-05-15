package no.uio.ifi.in2000.team8.data.locationforecast

import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.model.locationforecast.DataLF
import no.uio.ifi.in2000.team8.model.locationforecast.TimeserieLF

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
            // does not handle exceptions differently
            listOf()
        }
        return timeSeries.map { it.time to it.data }
    }


}
