package com.example.myapplication.data.locationForecast

import android.util.Log
import com.example.myapplication.data.utils.HTTPServiceHandler
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.TimeserieLF

interface LocationForecastRepository {
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>>
    suspend fun getWindDirection(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getTemperature(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWeatherIconsNextOneHour(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWeatherIconsNextSixHours(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWeatherIconsNextTwelveHours(surfArea: SurfArea): List<Pair<String, Double>>


}

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
): LocationForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>> {
        val timeSeries : List<TimeserieLF> = locationForecastDataSource.fetchLocationForecastData(surfArea).properties.timeseries
        return timeSeries.map { it.time to it.data }
    }

    private fun findWindSpeedFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_speed
    }

    private fun findWindSpeedOfGustFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_speed_of_gust
    }

    private fun findWindDirectionFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_from_direction
    }

    private fun findTemperatureFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.air_temperature
    }

    override suspend fun getWindDirection(surfArea: SurfArea): List<Pair<String, Double>> {
        // Henter timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til vindretning
        return timeSeriesForArea.map {it.first to findWindDirectionFromData(it.second)} ?: emptyList()

    }

    override suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<String, Double>> {
        // Henter timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til vindhastighet
        return timeSeriesForArea.map {it.first to findWindSpeedFromData(it.second)} ?: emptyList()


    }

    override suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<String, Double>> {
        // Henter timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til vindretning
        return timeSeriesForArea.map {it.first to findWindSpeedOfGustFromData(it.second)} ?: emptyList()

    }

    override suspend fun getTemperature(surfArea: SurfArea): List<Pair<String, Double>> {
        val timeSeriesForArea = getTimeSeries(surfArea)
        return timeSeriesForArea.map { it.first to findTemperatureFromData(it.second)} ?: emptyList()
    }

    override suspend fun getWeatherIconsNextOneHour(surfArea: SurfArea): List<Pair<String, Double>> {
        next_12_hours
    }

    override suspend fun getWeatherIconsNextSixHours(surfArea: SurfArea): List<Pair<String, Double>> {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherIconsNextTwelveHours(surfArea: SurfArea): List<Pair<String, Double>> {
        TODO("Not yet implemented")
    }

}
