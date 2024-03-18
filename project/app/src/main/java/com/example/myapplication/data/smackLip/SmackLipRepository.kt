package com.example.myapplication.data.smackLip

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
    suspend fun getWaveHeights(): List<Pair<List<Int>, Double>>
    suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>>
    suspend fun getTimeSeriesLF(): List<Pair<String, DataLF>>
    suspend fun getWindDirection(): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeed(): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeedOfGust(): List<Pair<List<Int>, Double>>

    abstract fun getTimeListFromTimeString(timeString : String): List<Int>

    suspend fun getForecastNext24Hours() : MutableList<MutableList<Pair<Int, List<Double>>>>
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
    override suspend fun getWaveHeights(): List<Pair<List<Int>, Double>> {
        val tmpWaveHeight = oceanforecastRepository.getWaveHeights()
        return tmpWaveHeight.map { waveHeight ->
            Pair(getTimeListFromTimeString(waveHeight.first), waveHeight.second)
        }
    }

    override suspend fun getTimeSeriesOF(): List<Pair<String, DataOF>> {
        return oceanforecastRepository.getTimeSeries()

    }


    //tar inn hele time-strengen på følgende format "time": "2024-03-13T18:00:00Z"
    //returnerer en liste slik: [år, måned, dag, time]
     override fun getTimeListFromTimeString(timeString : String) : List<Int> {
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

    override suspend fun getWindDirection(): List<Pair<List<Int>, Double>> {
        val tmpWindDirection = locationForecastRepository.getWindDirection()
        return tmpWindDirection.map { windDirection ->
            Pair(getTimeListFromTimeString(windDirection.first), windDirection.second)
        }
    }

    override suspend fun getWindSpeed(): List<Pair<List<Int>, Double>> {
        val tmpWindSpeed = locationForecastRepository.getWindSpeed()
        return tmpWindSpeed.map { windSpeed ->
            Pair(getTimeListFromTimeString(windSpeed.first), windSpeed.second)
        }
    }

    override suspend fun getWindSpeedOfGust(): List<Pair<List<Int>, Double>> {
        val tmpWindGust = locationForecastRepository.getWindSpeedOfGust()
        return tmpWindGust.map { windGust ->
            Pair(getTimeListFromTimeString(windGust.first), windGust.second)
        }

    }

    //en funksjon som returnerer en liste med par av
    // 1. dato og
    // 2. dataene for de 24 timene den dagen, som består av en liste med par av
            // 1. timen og
            // 2. en liste med de fire dataene for den timen
                    // [waveHeight, windDirection, windSpeed, windSpeedOfGust]

    //totalt: List<Pair<List<Int>, List<Pair<Int, List<Double>>>>

    override suspend fun getForecastNext24Hours() : MutableList<MutableList<Pair<Int, List<Double>>>> {
        val allDays24Hours : MutableList<MutableList<Pair<Int, List<Double>>>> = mutableListOf()

        val waveHeight = getWaveHeights()
        val windDirection = getWindDirection()
        val windSpeed = getWindSpeed()
        val windSpeedOfGust = getWindSpeedOfGust()

        for (i in 0 until 7) { //24 timer de neste 7 dagene

            val date : List<Int> = listOf(waveHeight[i].first[1], waveHeight[i].first[2]) //dato = [mnd, dag]
            val forecast24HoursList : MutableList<Pair<Int, List<Double>>> = mutableListOf() //data for hver time den dagen = [(time, [waveHeight, windDirection, windSpeed, windSpeedOfGust])]

            for (j in 0 until  24) {
                val forecastOneHour = Pair(
                    waveHeight[i].first[3], //timen
                    listOf(                 //værmelding den timen
                        waveHeight[i].second,
                        windDirection[i].second,
                        windSpeed[i].second,
                        windSpeedOfGust[i].second
                    )
                )
                forecast24HoursList.add(forecastOneHour) //legger inn ny entry for den enkelte timen, totalt 24 ganger per dag

            }
            allDays24Hours.add(forecast24HoursList)
        }

        return allDays24Hours
    }

    }