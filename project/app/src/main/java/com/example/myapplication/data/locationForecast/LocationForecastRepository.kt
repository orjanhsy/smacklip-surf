package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.locationforecast.LocationForecast
import com.example.myapplication.data.locationForecast.LocationForecastDataSource
interface LocationForecastRepository{
    suspend fun getLocationForecast(): LocationForecast
}

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
): LocationForecastRepository{
    override suspend fun getLocationForecast(): LocationForecast {
        return locationForecastDataSource.fetchLocationForecastData()
    }
}