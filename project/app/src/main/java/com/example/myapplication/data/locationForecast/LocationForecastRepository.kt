package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.TimeserieLF

interface LocationForecastRepository {
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>>
    suspend fun getWindDirection(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<String, Double>>
}

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
): LocationForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>> {
        val timeSeries: List<TimeserieLF> =
            locationForecastDataSource.fetchLocationForecastData(surfArea).properties.timeseries
        return timeSeries.map { it.time to it.data }
    }

    private suspend fun getWindData(surfArea: SurfArea, getData: (DataLF) -> Double): List<Pair<String, Double>> {
        val timeSeries = getTimeSeries(surfArea)
        return timeSeries.map { it.first to getData(it.second) }
    }

    override suspend fun getWindDirection(surfArea: SurfArea): List<Pair<String, Double>> {
        return getWindData(surfArea) { dataLF -> dataLF.instant.details.wind_from_direction }
    }

    override suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<String, Double>> {
        return getWindData(surfArea) { dataLF -> dataLF.instant.details.wind_speed }

    }

    override suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<String, Double>> {
        return getWindData(surfArea) { dataLF -> dataLF.instant.details.wind_speed_of_gust }
    }
}
