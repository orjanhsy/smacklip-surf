package com.example.myapplication.data.smackLip

import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.PointForecast


interface SmackLipRepository {
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features>
    suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getTimeSeriesOF(surfArea: SurfArea): List<Pair<String, DataOF>>
    suspend fun getTimeSeriesLF(surfArea: SurfArea): List<Pair<String, DataLF>>
    suspend fun getWindDirection(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    abstract fun getTimeListFromTimeString(timeString : String): List<Int>

    //suspend fun getForecastNext24Hours() : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>>

    suspend fun getDataForOneDay(day : Int, surfArea: SurfArea): List<Pair<List<Int>, List<Double>>>

    suspend fun getDataForTheNext7Days(surfArea: SurfArea): MutableList<List<Pair<List<Int>, List<Double>>>>

    suspend fun getAllWaveForecastsNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>>

    suspend fun getWaveForecastsNext3DaysForArea(surfArea: SurfArea): List<Pair<Double?, Double?>>

    suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>>
}

class SmackLipRepositoryImpl (
    private val metAlertsRepository: MetAlertsRepositoryImpl = MetAlertsRepositoryImpl(),
    private val locationForecastRepository: LocationForecastRepository = LocationForecastRepositoryImpl(),
    private  val oceanForecastRepository: OceanforecastRepository = OceanforecastRepositoryImpl(),
    private val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl()
): SmackLipRepository {

    //MET
    override suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features> {
        return metAlertsRepository.getRelevantAlertsFor(surfArea)
    }


    //OF
    override suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<List<Int>, Double>> {
        val tempWaveHeight = oceanForecastRepository.getWaveHeights(surfArea)
        return tempWaveHeight.map { waveHeight ->
            Pair(getTimeListFromTimeString(waveHeight.first), waveHeight.second)
        }

    }

    override suspend fun getTimeSeriesOF(surfArea: SurfArea): List<Pair<String, DataOF>> {
        return oceanForecastRepository.getTimeSeries(surfArea)

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
    override suspend fun getTimeSeriesLF(surfArea: SurfArea): List<Pair<String, DataLF>> {
        return locationForecastRepository.getTimeSeries(surfArea)
    }

    override suspend fun getWindDirection(surfArea: SurfArea): List<Pair<List<Int>, Double>> {
        val tmpWindDirection = locationForecastRepository.getWindDirection(surfArea)
        return tmpWindDirection.map { windDirection ->
            Pair(getTimeListFromTimeString(windDirection.first), windDirection.second)
        }
    }

    override suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<List<Int>, Double>> {
        val tmpWindSpeed = locationForecastRepository.getWindSpeed(surfArea)
        return tmpWindSpeed.map { windSpeed ->
            Pair(getTimeListFromTimeString(windSpeed.first), windSpeed.second)
        }
    }

    override suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<List<Int>, Double>> {
        val tmpWindGust = locationForecastRepository.getWindSpeedOfGust(surfArea)
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

    //sender med dato (dag)
    //metoden finner felles tider for alle dataenelistene og lager et par av denne tiden og en liste md de 4 dataene
    //setter sammen alle parene til en liste
    //sitter til slutt igjen med en liste bestående av par med tid og tilhørende data for den tiden
    //metoden fungerer uavhengig av hvor mange tidspunkt det er data for

    //List<Pair<Time, DataAtTime>>>  .size= 0..24 ('i dag' vil vise så mange timer det er igjen av døgnet, resten vil vise 24 timer.)
    override suspend fun getDataForOneDay(day : Int, surfArea: SurfArea): List<Pair<List<Int>, List<Double>>> {
        val waveHeight :  List<Pair<List<Int>, Double>> = getWaveHeights(surfArea).filter { waveHeight -> waveHeight.first[2] == day }
        val windDirection :  List<Pair<List<Int>, Double>> = getWindDirection(surfArea).filter { windDirection -> windDirection.first[2] == day }
        val windSpeed :  List<Pair<List<Int>, Double>> = getWindSpeed(surfArea).filter { windSpeed -> windSpeed.first[2] == day }
        val windSpeedOfGust :  List<Pair<List<Int>, Double>> = getWindSpeedOfGust(surfArea).filter { gust -> gust.first[2] == day }

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


    //metoden kaller getDataForOneDay 7 ganger fra og med i dag, og legger til listen med data for hver dag
    //inn i resListe som til slutt består av data med tidspunkt og data for alle 7 dager
                                                                    //Days<Hours<Pair<Time, DataAtTime>>>>    .size=7
    override suspend fun getDataForTheNext7Days(surfArea: SurfArea): MutableList<List<Pair<List<Int>, List<Double>>>> {
        val today = getWaveHeights(surfArea)[0].first[2] //regner med at det er dumt med et helt api-kall bare for å hente dagens dato
        val resList = mutableListOf<List<Pair<List<Int>, List<Double>>>>()
        for (i in today until today+7){
            resList.add(getDataForOneDay(i, surfArea))
        }
        return resList
    }

    override suspend fun getAllWaveForecastsNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>> {
        return try {
            waveForecastRepository.allRelevantWavePeriodAndDirNext3DaysHardCoded()
        } catch (e: Exception) {
            waveForecastRepository.allRelevantWavePeriodAndDirNext3Days()
        }
    }

    override suspend fun getWaveForecastsNext3DaysForArea(surfArea: SurfArea): List<Pair<Double?, Double?>> {
        return waveForecastRepository.waveDirAndPeriodNext3DaysForArea(surfArea.modelName, surfArea.pointId)
    }

    override suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>> {
        return oceanForecastRepository.getTimeSeriesDayByDay(surfArea)
    }

}