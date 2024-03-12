package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.Timeserie

class LocationForecastRepository(private val locationForecastDataSource: LocationForecastDataSource){

    suspend fun getTimeSeries(): List<Pair<String, DataLF>>{
        val timeSeries: List<Timeserie> = locationForecastDataSource.fetchLocationForecastData().properties.timeseries
        return timeSeries.map{it.time to it.data}
    }

}
