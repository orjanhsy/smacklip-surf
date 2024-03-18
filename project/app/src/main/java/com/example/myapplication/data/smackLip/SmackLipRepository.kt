package com.example.myapplication.data.smackLip

import com.example.myapplication.data.locationForecast.LocationForecastDataSource
import com.example.myapplication.data.locationForecast.LocationForecastRepository

import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastDataSource
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl

import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF
import java.util.Date


interface SmackLipRepository {
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features>
    fun getWaveHeights(timeSeries: List<Pair<String, DataOF>>): List<Pair<String, Double>>
    suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>>
    suspend fun getTimeSeriesLF(): List<Pair<String, DataLF>>
    suspend fun getWindDirection(): List<Pair<String, Double>>
    suspend fun getWindSpeed(): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(): List<Pair<String, Double>>
}

class SmackLipRepositoryImpl (
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl(),
    private val locationForecastRepository: LocationForecastRepository = LocationForecastRepositoryImpl(),
    private  val oceanforecastRepository: OceanforecastRepository = OceanforecastRepositoryImpl()

    ): SmackLipRepository {

    //MET
    override suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features> {
        return metAlertsRepository.getRelevantAlertsFor(surfArea)
    }

    //OF
    override fun getWaveHeights(timeSeries: List<Pair<String, DataOF>>): List<Pair<String, Double>> {
        return oceanforecastRepository.getWaveHeights(timeSeries)
    }

    override suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>> {
        return oceanforecastRepository.getTimeSeries()

    }

    //tar inn hele time-strengen på følgende format "time": "2024-03-13T18:00:00Z"
    //returnerer en liste slik: [år, måned, dag, time]
    fun getDateFromTimeString(timeString : String) : List<Int> {
        return listOf(
        timeString.substring(0, 4).toInt(),
        timeString.substring(5, 7).toInt(),
        timeString.substring(8, 10).toInt(),
        timeString.substring(11, 13).toInt())
    }



    //LF
    override suspend fun getTimeSeriesLF(): List<Pair<String, DataLF>> {
        return locationForecastRepository.getTimeSeries()
    }

    override suspend fun getWindDirection(): List<Pair<String, Double>> {
        return locationForecastRepository.getWindDirection()
    }

    override suspend fun getWindSpeed(): List<Pair<String, Double>> {
        return locationForecastRepository.getWindSpeed()
    }

    override suspend fun getWindSpeedOfGust(): List<Pair<String, Double>> {
        return locationForecastRepository.getWindSpeedOfGust()
    }
    }