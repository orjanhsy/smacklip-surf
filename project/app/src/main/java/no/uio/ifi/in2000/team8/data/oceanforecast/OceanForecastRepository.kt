package no.uio.ifi.in2000.team8.data.oceanforecast

import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea

interface OceanForecastRepository{
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>>
}

class OceanForecastRepositoryImpl(
    private val dataSource: OceanForecastDataSource = OceanForecastDataSource()
): OceanForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>> {
        //gets list of timeseries objects, containing time and data
        val timeSeries: List<TimeserieOF> = try { dataSource.fetchOceanforecast(surfArea).properties.timeseries }
        catch (e: Exception) {
            // handles http exceptions from data source
            listOf()
        }

        //returns a list of time mapped to data
        return timeSeries.map { it.time to it.data }
    }
}