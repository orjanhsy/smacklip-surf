package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.Timeserie

class LocationForecastRepository(private val locationForecastDataSource: LocationForecastDataSource){

    suspend fun getTimeSeries(): List<Pair<String, DataLF>>{
        val timeSeries: List<Timeserie> = locationForecastDataSource.fetchLocationForecastData().properties.timeseries
        return timeSeries.map{it.time to it.data}
    }
    suspend fun getWindDirection(): List<Pair<String, Double>> {
        val timeSeries = getTimeSeries()
        return timeSeries.map { it.first to findWindFromDirection(it.second) }
    }

    suspend fun getWindSpeed(): List<Pair<String, Double>> {
        val timeSeries = getTimeSeries()
        return timeSeries.map{ it.first to findWindSpeed(it.second)}
    }

    suspend fun getWindSpeedOfGust(): List<Pair<String, Double>>{
        val timeSeries = getTimeSeries()
        return timeSeries.map { it.first to findWindSpeedOfGust(it.second) }
    }

    private fun findWindFromDirection(dataLF: DataLF): Double {
        return dataLF.instant.details.wind_from_direction
    }
    private fun findWindSpeed(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_speed
    }

    private fun findWindSpeedOfGust(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_speed_of_gust

    }

}
