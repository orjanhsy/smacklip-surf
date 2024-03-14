package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.TimeserieLF

class LocationForecastRepository(private val locationForecastDataSource: LocationForecastDataSource) {

    suspend fun getTimeSeries(): List<Pair<String, DataLF>> {
        val timeSeries: List<TimeserieLF> =
            locationForecastDataSource.fetchLocationForecastData().properties.timeseries
        return timeSeries.map { it.time to it.data }
    }

    private suspend fun getWindData(getData: (DataLF) -> Double): List<Pair<String, Double>> {
        val timeSeries = getTimeSeries()
        return timeSeries.map { it.first to getData(it.second) }
    }

    suspend fun getWindDirection(): List<Pair<String, Double>> {
        return getWindData { dataLF -> dataLF.instant.details.wind_from_direction }
    }

    suspend fun getWindSpeed(): List<Pair<String, Double>> {
        return getWindData { dataLF -> dataLF.instant.details.wind_speed }
    }

    suspend fun getWindSpeedOfGust(): List<Pair<String, Double>> {
        return getWindData { dataLF -> dataLF.instant.details.wind_speed_of_gust }
    }

}
