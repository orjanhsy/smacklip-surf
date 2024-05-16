package no.uio.ifi.in2000.team8.data.oceanforecast

import no.uio.ifi.in2000.team8.model.oceanforecast.DataOF
import no.uio.ifi.in2000.team8.model.oceanforecast.TimeserieOF
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea

interface OceanForecastRepository{
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>>
}

class OceanForecastRepositoryImpl(
    private val dataSource: OceanForecastDataSource = OceanForecastDataSource()
): OceanForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>> {
        //gets list of timeSeries objects, containing time and data
        val timeSeries: List<TimeserieOF> = try {
            dataSource.fetchOceanForecast(surfArea).properties.timeseries
        }
        catch (e: Exception) {
            // does not handle errors differently
            emptyList()
        }

        //returns a list of time mapped to data
        return timeSeries.map { it.time to it.data }
    }
}