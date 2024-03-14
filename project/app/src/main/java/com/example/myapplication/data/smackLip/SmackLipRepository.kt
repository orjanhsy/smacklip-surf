package com.example.myapplication.data.smackLip

import com.example.myapplication.data.locationForecast.LocationForecastDataSource
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastDataSource
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF


interface SmackLipRepository {
    fun getRelevantAlertsFor(surfArea: SurfArea, allFeatures: List<Features>): List<Features>
    fun getWaveHeights(timeSeries: List<Pair<String, DataOF>>): List<Pair<String, Double>>
    suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>>
    suspend fun getTimeSeriesLF(): List<Pair<String, DataLF>>
    suspend fun getWindDirection(): List<Pair<String, Double>>
    suspend fun getWindSpeed(): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(): List<Pair<String, Double>>
}

class SmackLipRepositoryImpl (
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl(),
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource(),
    private val locationForecastRepository: LocationForecastRepository = LocationForecastRepository(locationForecastDataSource),
    private  val oceanforecastRepository: OceanforecastRepository = OceanforecastRepositoryImpl()


    ): SmackLipRepository {

        //MET
    override fun getRelevantAlertsFor(surfArea: SurfArea, allFeatures: List<Features>): List<Features> {
        return metAlertsRepository.getRelevantAlertsFor(surfArea, allFeatures)
     }

    //OF
    override fun getWaveHeights(timeSeries: List<Pair<String, DataOF>>): List<Pair<String, Double>> {
        return oceanforecastRepository.getWaveHeights(timeSeries)
    }

    override suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>> {
        return oceanforecastRepository.getTimeSeries()

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