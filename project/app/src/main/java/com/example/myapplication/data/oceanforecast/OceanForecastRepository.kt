package com.example.myapplication.data.oceanforecast

import android.util.Log
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea

private const val TAG = "OFREPO"
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