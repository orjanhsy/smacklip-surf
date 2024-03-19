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

    suspend fun getForecastNext24Hours() : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>>

    suspend fun getDataForOneDay(day : Int): List<Pair<List<Int>, List<Double>>>

    suspend fun getDataForTheNext7Days(): MutableList<List<Pair<List<Int>, List<Double>>>>
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

    override suspend fun getForecastNext24Hours() : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>> {
        val allDays24Hours : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>> = mutableListOf()

        //Pair<List<Int>, Pair<Int, List<Double>>>

        val waveHeight = getWaveHeights()
        val windDirection = getWindDirection()
        val windSpeed = getWindSpeed()
        val windSpeedOfGust = getWindSpeedOfGust()

        println(waveHeight.size)
        println(windDirection.size)
        println(windSpeed.size)
        println(windSpeedOfGust.size)

        var listIndex : Int = 0

        for (i in 0 until 3) { //24 timer de neste 3 dagene

            val date : List<Int> = listOf(waveHeight[listIndex].first[1], waveHeight[listIndex].first[2]) //dato = [mnd, dag]
            val forecast24HoursList : MutableList<Pair<List<Int>, Pair<Int, List<Double>>>> = mutableListOf() //data for hver time den dagen = [(time, [waveHeight, windDirection, windSpeed, windSpeedOfGust])]

            var nextIndexCounter : Int = 0

            for (j in 0  until  24-waveHeight[listIndex].first[3]) {
                val forecastOneHour = Pair(
                    waveHeight[j+listIndex].first[3], //timen
                    listOf(                 //værmelding den timen
                        waveHeight[j+listIndex].second,
                        windDirection[j+listIndex].second,
                        windSpeed[j+listIndex].second,
                        windSpeedOfGust[j+listIndex].second
                    )
                )

                nextIndexCounter ++

                println("sjekke tider:")
                println(waveHeight[j].first.toString())
                println(windDirection[j].first.toString())
                println(windSpeed[j].first.toString())
                println(windSpeedOfGust[j].first.toString())
                println()
                forecast24HoursList.add(Pair(date, forecastOneHour)) //legger inn ny entry for den enkelte timen, totalt 24 ganger per dag

            }

            listIndex += nextIndexCounter
            allDays24Hours.add(forecast24HoursList)
        }

        return allDays24Hours
    }


    override suspend fun getDataForOneDay(day : Int): List<Pair<List<Int>, List<Double>>> {
        val waveHeight :  List<Pair<List<Int>, Double>> = getWaveHeights().filter { waveHeight -> waveHeight.first[2] == day }
        val windDirection :  List<Pair<List<Int>, Double>> = getWindDirection().filter { windDirection -> windDirection.first[2] == day }
        val windSpeed :  List<Pair<List<Int>, Double>> = getWindSpeed().filter { windSpeed -> windSpeed.first[2] == day }
        val windSpeedOfGust :  List<Pair<List<Int>, Double>> = getWindSpeedOfGust().filter { gust -> gust.first[2] == day }

        val dataList = waveHeight.map {
            val time : List<Int> = it.first
            try {
                val windDirectionAtTime = windDirection.first {data -> data.first.equals(time)}.second
                val windSpeedAtTime = windSpeed.first() {data -> data.first.equals(time)}.second
                val windSpeedOfGustAtTime = windSpeedOfGust.first() {data -> data.first.equals(time)}.second
                val dataAtTime : List<Double> = listOf(it.second, windDirectionAtTime, windSpeedAtTime, windSpeedOfGustAtTime)
                Pair(time, dataAtTime)

            }catch (_: NoSuchElementException){
                //fortsetter - må fortsette i tilfelle det er flere tidspunkt som matcher
            }
        }
        return dataList.filterIsInstance<Pair<List<Int>, List<Double>>>() //fjerner elementer som blir Kotlin.Unit pga manglende time-match
    }


    override suspend fun getDataForTheNext7Days(): MutableList<List<Pair<List<Int>, List<Double>>>> {
        val today = getWaveHeights()[0].first[2]
        val resList = mutableListOf<List<Pair<List<Int>, List<Double>>>>()
        for (i in today until today+7){
            resList.add(getDataForOneDay(i))
        }
        return resList
    }

}