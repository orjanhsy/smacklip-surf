package com.example.myapplication.data.smackLip

import com.example.myapplication.R
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.model.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF


interface SmackLipRepository {
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features>
    fun getWaveHeights(timeSeries: List<Pair<String, DataOF>>): List<Pair<String, Double>>
    suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>>
    suspend fun getTimeSeriesLF(): List<Pair<String, DataLF>>
    suspend fun getWindDirection(): List<Pair<String, Double>>
    suspend fun getWindSpeed(): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(): List<Pair<String, Double>>
    suspend fun getIconBasedOnAwarenessLevel(awarenessLevel: String): Int
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

    override suspend fun getIconBasedOnAwarenessLevel(awarenessLevel: String): Int {
        val firstChar = awarenessLevel.firstOrNull()?.toString()

        return when (firstChar) {
            "2" -> R.drawable.icon_awareness_yellow
            "3" -> R.drawable.icon_awareness_orange
            "4" -> R.drawable.icon_awareness_red
            else -> R.drawable.icon_awareness_default
        }
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