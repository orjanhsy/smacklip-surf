package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.locationforecast.Data
import com.example.myapplication.model.locationforecast.Timeserie

class LocationForecastRepository(private val locationForecastDataSource: LocationForecastDataSource){

    suspend fun getTimeSeries(): List<Pair<String, Data>>{
        val timeSeries: List<Timeserie> = locationForecastDataSource.fetchLocationForecastData().properties.timeseries
        return timeSeries.map{it.time to it.data}
    }

}
