package com.example.myapplication.data.locationForecast

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
        val timeSeries : List<TimeserieLF> = locationForecastDataSource.fetchLocationForecastData(surfArea).properties.timeseries
        return timeSeries.map { it.time to it.data }
    }


}
