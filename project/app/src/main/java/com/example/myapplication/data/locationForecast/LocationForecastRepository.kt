package com.example.myapplication.data.locationForecast

import android.util.Log
import com.example.myapplication.data.helpers.HTTPServiceHandler
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
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource(path = HTTPServiceHandler.OCEAN_FORECAST_URL)
): LocationForecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>> {
        val locationForecast = locationForecastDataSource.fetchLocationForecastData(surfArea)
        Log.d("LocationTimeSeries", "Location forecast for ${surfArea.locationName}: $locationForecast" )
        return if(locationForecast?.properties?.timeseries != null) {
            locationForecast.properties.timeseries.map { it.time to it.data }
        } else {
            emptyList()
        }
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

    private fun inArea(lat: Double, lon: Double, surfArea: SurfArea, radius: Double = 0.1): Boolean{
        return (
                lat in surfArea.lat - radius..surfArea.lat + radius &&
                lon in surfArea.lon - radius..surfArea.lon + radius
                )
    }
}
