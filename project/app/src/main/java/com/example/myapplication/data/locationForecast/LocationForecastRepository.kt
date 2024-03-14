package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.TimeserieLF

interface LocationForecastRepository {
    suspend fun getTimeSeries(): List<Pair<String, DataLF>>
    suspend fun getWindDirection(): List<Pair<String, Double>>
    suspend fun getWindSpeed(): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(): List<Pair<String, Double>>
}

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()) : LocationForecastRepository {

    override suspend fun getTimeSeries(): List<Pair<String, DataLF>> {
        val timeSeries: List<TimeserieLF> =
            locationForecastDataSource.fetchLocationForecastData().properties.timeseries
        return timeSeries.map { it.time to it.data }
    }

    private suspend fun getWindData(getData: (DataLF) -> Double): List<Pair<String, Double>> {
        val timeSeries = getTimeSeries()
        return timeSeries.map { it.first to getData(it.second) }
    }

    override suspend fun getWindDirection(): List<Pair<String, Double>> {
        return getWindData { dataLF -> dataLF.instant.details.wind_from_direction }
    }

    override suspend fun getWindSpeed(): List<Pair<String, Double>> {
        return getWindData { dataLF -> dataLF.instant.details.wind_speed }

    }

    override suspend fun getWindSpeedOfGust(): List<Pair<String, Double>> {
        return getWindData { dataLF -> dataLF.instant.details.wind_speed_of_gust }
    }
}
