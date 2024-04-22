package com.example.myapplication.data.smackLip

import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.conditions.ConditionDescriptions
import com.example.myapplication.model.conditions.Conditions
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.surfareas.SurfArea
import kotlin.math.abs


interface SmackLipRepository {
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features>
    suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    suspend fun getWaveDirections(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    suspend fun getTimeSeriesOF(surfArea: SurfArea): List<Pair<String, DataOF>>
    suspend fun getTimeSeriesLF(surfArea: SurfArea): List<Pair<String, DataLF>>
    suspend fun getWindDirection(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getAirTemperature(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getSymbolCode(surfArea: SurfArea): List<Pair<List<Int>, String>>
    abstract fun getTimeListFromTimeString(timeString : String): List<Int>
    //suspend fun getForecastNext24Hours() : MutableList<MutableList<Pair<List<Int>, Pair<Int, List<Double>>>>>
    suspend fun getDataForOneDay(day : Int, surfArea: SurfArea): List<Pair<List<Int>, List<Any>>>
    //suspend fun getSymbolCodeForOneDay(day : Int, surfArea: SurfArea): List<Pair<List<Int>, List<String>>>
    suspend fun getDataForTheNext7Days(surfArea: SurfArea): MutableList<List<Pair<List<Int>, List<Any>>>>
    suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>>


    // waveforecast
    suspend fun getAllWaveForecastsNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>>
    suspend fun getWaveForecastsNext3DaysForArea(surfArea: SurfArea): List<Pair<Double?, Double?>>
    suspend fun getAllWavePeriodsNext3Days(): Map<SurfArea, List<Double?>>
    suspend fun getWavePeriodsNext3DaysForArea(surfArea: SurfArea): List<Double?>

    fun getConditionStatus(
        location: SurfArea,
        windSpeed: Double,
        windGust: Double,
        windDir: Double,
        waveHeight: Double,
        waveDir: Double,
        wavePeriod: Double,
        alerts: List<Features>
    ): String

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
    override suspend fun getWaveDirections(surfArea: SurfArea): List<Pair<List<Int>, Double>> {
        val tempWaveDir = oceanForecastRepository.getWaveDirections(surfArea)
        return tempWaveDir.map { waveDir ->
            Pair(getTimeListFromTimeString(waveDir.first), waveDir.second)
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

    override suspend fun getAirTemperature(surfArea: SurfArea): List<Pair<List<Int>, Double>> {
        val tmpTemperature = locationForecastRepository.getTemperature(surfArea)
        return tmpTemperature.map { temp ->
            Pair(getTimeListFromTimeString(temp.first), temp.second)
        }
    }

    override suspend fun getSymbolCode(surfArea: SurfArea): List<Pair<List<Int>, String>> {
        //ønsker next_one_hour der det finnes, hvis ikke next_six_hours
        val nextOneHour : List<Pair<String, String>> = locationForecastRepository.getSymbolCodeNextOneHour(surfArea)
        val nextSixHours : List<Pair<String, String>> = locationForecastRepository.getSymbolCodeNextSixHours(surfArea)
        val symbolCodesCombined = mergeListsPreserveFirst(nextOneHour, nextSixHours)
        return symbolCodesCombined.map { symbol ->
            Pair(getTimeListFromTimeString(symbol.first), symbol.second)
        }
    }

    //denne gjør at next_one_hour brukes hvis den finnes, hvis ikke brukes next_six_hours
    private fun mergeListsPreserveFirst(list1: List<Pair<String, String>>, list2: List<Pair<String, String>>): List<Pair<String, String>> {
        // konverterer første list til map - raskere søking
        val map1 = list1.toMap()

        // konverterer andre liste til map og tar bort nøkkel-verdi-par der nøkkelen finnes i liste 1
        val map2 = list2.toMap().filterKeys { key -> key !in map1.keys }

        // setter sammen mapsene
        val combinedMap = map1 + map2

        //konverterer tilbake til liste igjen
        return combinedMap.toList()
    }



    //en funksjon som returnerer en liste med par av
    // 1. dato og
    // 2. dataene for de 24 timene den dagen, som består av en liste med par av
    // 1. timen og
    // 2. en liste med de fire dataene for den timen
    // [waveHeight, windDirection, windSpeed, windSpeedOfGust, temperature, symbolCode]

    //totalt: List<Pair<List<Int>, List<Pair<Int, List<Double>>>>

    //sender med dato (dag)
    //metoden finner felles tider for alle dataenelistene og lager et par av denne tiden og en liste md de 4 dataene
    //setter sammen alle parene til en liste
    //sitter til slutt igjen med en liste bestående av par med tid og tilhørende data for den tiden
    //metoden fungerer uavhengig av hvor mange tidspunkt det er data for

    //List<Pair<Time, DataAtTime>>>  .size= 0..24 ('i dag' vil vise så mange timer det er igjen av døgnet, resten vil vise 24 timer.)
    override suspend fun getDataForOneDay(day : Int, surfArea: SurfArea): List<Pair<List<Int>, List<Any>>> {
        val waveHeight :  List<Pair<List<Int>, Double>> = getWaveHeights(surfArea).filter { waveHeight -> waveHeight.first[2] == day }
        val waveDirection: List<Pair<List<Int>, Double>> = getWaveDirections(surfArea).filter {waveDir -> waveDir.first[2] == day}
        val windDirection :  List<Pair<List<Int>, Double>> = getWindDirection(surfArea).filter { windDirection -> windDirection.first[2] == day }
        val windSpeed :  List<Pair<List<Int>, Double>> = getWindSpeed(surfArea).filter { windSpeed -> windSpeed.first[2] == day }
        val windSpeedOfGust :  List<Pair<List<Int>, Double>> = getWindSpeedOfGust(surfArea).filter { gust -> gust.first[2] == day }
        val airTemperature :  List<Pair<List<Int>, Double>> = getAirTemperature(surfArea).filter { temp -> temp.first[2] == day}
        val symbolCode = getSymbolCode(surfArea).filter { symbol -> symbol.first[2] == day }

        val dataList = waveHeight.map {
            val time : List<Int> = it.first
            try {
                val waveDirectionAtTime = waveDirection.first {data -> data.first.equals(time)}.second
                val windDirectionAtTime = windDirection.first {data -> data.first.equals(time)}.second
                val windSpeedAtTime = windSpeed.first() {data -> data.first.equals(time)}.second
                val windSpeedOfGustAtTime = windSpeedOfGust.first() {data -> data.first.equals(time)}.second
                val airTemperatureAtTime = airTemperature.first() {data -> data.first.equals(time)}.second
                val symbolCodeAtTime = symbolCode.first() {data -> data.first.equals(time)}.second
                val dataAtTime : List<Any> = listOf(it.second, waveDirectionAtTime, windDirectionAtTime, windSpeedAtTime, windSpeedOfGustAtTime, airTemperatureAtTime, symbolCodeAtTime)
                Pair(time, dataAtTime)

            }catch (_: NoSuchElementException){
                //fortsetter - må fortsette i tilfelle det er flere tidspunkt som matcher
            }
        }
        return dataList.filterIsInstance<Pair<List<Int>, List<Any>>>() //fjerner elementer som blir Kotlin.Unit pga manglende time-match
    }

    /*
    override suspend fun getSymbolCodeForOneDay(day : Int, surfArea: SurfArea): List<Pair<List<Int>, List<String>>>{
        val symbolCode = getSymbolCode(surfArea).filter { symbol -> symbol.first[2] == day }

    }*/


    //metoden kaller getDataForOneDay 7 ganger fra og med i dag, og legger til listen med data for hver dag
    //inn i resListe som til slutt består av data med tidspunkt og data for alle 7 dager
    //Days<Hours<Pair<Time, DataAtTime>>>>    .size=7
    override suspend fun getDataForTheNext7Days(surfArea: SurfArea): MutableList<List<Pair<List<Int>, List<Any>>>> {
        val today = getWaveHeights(surfArea)[0].first[2] //regner med at det er dumt med et helt api-kall bare for å hente dagens dato
        val resList = mutableListOf<List<Pair<List<Int>, List<Any>>>>()
        for (i in today until today+7){
            resList.add(getDataForOneDay(i, surfArea))
        }
        return resList
    }

    override suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>> {
        return oceanForecastRepository.getTimeSeriesDayByDay(surfArea)
    }

    // mapper hvert enkelt surfarea til en liste med (bølgeretning, bølgeperiode) lik de i 'getWaveForecastNext3DaysForArea()' under.
    override suspend fun getAllWaveForecastsNext3Days(): Map<SurfArea, List<Pair<Double?, Double?>>> {
        return try {
            waveForecastRepository.allRelevantWavePeriodAndDirNext3DaysHardCoded()
        } catch (e: Exception) {
            waveForecastRepository.allRelevantWavePeriodAndDirNext3Days()
        }
    }

    // liste med pair(bølgeretning, bølgeperiode), .size in 18..20 (3timers intervaller, totalt 60 timer). Vet ikke hvorfor den av og til er 19 lang, da er det i så fall bare 57 timer forecast.
    override suspend fun getWaveForecastsNext3DaysForArea(surfArea: SurfArea): List<Pair<Double?, Double?>> {
        return waveForecastRepository.waveDirAndPeriodNext3DaysForArea(
            surfArea.modelName,
            surfArea.pointId
        )
    }


    // wf men bare med waveperiods, i motsetning (wavedir, waveperiod) over.
    override suspend fun getAllWavePeriodsNext3Days(): Map<SurfArea, List<Double?>> {

        val wavePeriods = getAllWaveForecastsNext3Days().entries.associate{it.key to it.value.map{ data -> data.second}}
        val formattedWavePeriods: MutableMap<SurfArea, List<Double?>> = mutableMapOf()
        SurfArea.entries.forEach { surfArea ->
            formattedWavePeriods[surfArea] = wavePeriods[surfArea]!!.flatMap { listOf(it, it, it) }
        }
        return formattedWavePeriods
    }

    override suspend fun getWavePeriodsNext3DaysForArea(surfArea: SurfArea): List<Double?> {
        val wavePeriods = getWaveForecastsNext3DaysForArea(surfArea).map{it.second}

        // format to hour-by-hour
        val reformattedWavePeriods = mutableListOf<Double?>()
        wavePeriods.forEach{
            reformattedWavePeriods.add(it)
            reformattedWavePeriods.add(it)
            reformattedWavePeriods.add(it)
        }
        return reformattedWavePeriods
    }

    private fun withinDir(optimalDir: Double, actualDir: Double, acceptedOffset: Double): Boolean {

        return abs(optimalDir - actualDir) !in acceptedOffset .. 360 - acceptedOffset
    }
    override fun getConditionStatus(
        location: SurfArea,
        windSpeed: Double,
        windGust: Double,
        windDir: Double,
        waveHeight: Double,
        waveDir: Double,
        wavePeriod: Double,
        alerts: List<Features>
    ): String {
        var conditionStatus: ConditionDescriptions = ConditionDescriptions.DECENT

        // conditions that result in poor status regardless of other variables.
        if (
            windSpeed >= Conditions.WIND_SPEED_UPPER_BOUND.value
            || waveHeight <= Conditions.WAVE_HEIGHT_LOWER_BOUND.value
            || waveHeight >= Conditions.WAVE_HEIGHT_UPPER_BOUND.value
            || wavePeriod <= Conditions.WAVE_PERIOD_LOWER_BOUND.value
            || alerts.isNotEmpty()
        ) {
            conditionStatus = ConditionDescriptions.POOR
            return conditionStatus.description
        }

        val status = mutableMapOf<String, Double>()
        status["windSpeed"] = when {
            windSpeed < Conditions.WIND_SPEED_GREAT_UPPER_BOUND.value -> 1.0
            windSpeed < Conditions.WIND_SPEED_DECENT_UPPER_BOUND.value -> 2.0
            else -> 3.0
        }
        status["windGust"] = status["windSpeed"]!!

        val windDirFactor = when(withinDir(location.optimalWindDir, windDir, Conditions.WIND_DIR_GREAT_DEVIATION.value)) {
            true -> 1.0
            false -> if (withinDir(location.optimalWindDir, windDir, Conditions.WIND_DIR_DECENT_DEVIATION.value)) 1.2 else 1.5
        }

        status["windSpeed"] = status["windSpeed"]!! * windDirFactor

        status["waveDir"] = when(withinDir(location.optimalWaveDir, waveDir, Conditions.WAVE_DIR_GREAT_DEVIATION.value)) {
            true -> 1.0
            false -> if (withinDir(location.optimalWaveDir, waveDir, Conditions.WAVE_DIR_DECENT_DEVIATION.value)) 2.0 else 3.0
        }

        status["wavePeriod"] = when {
            wavePeriod > Conditions.WAVE_PERIOD_GREAT_LOWER_BOUND.value -> 1.0
            wavePeriod in Conditions.WAVE_PERIOD_DECENT_LOWER_BOUND.value ..
                    Conditions.WAVE_PERIOD_GREAT_LOWER_BOUND.value -> 2.0
            else -> 3.0
        }

        val averageStatus = status.values.sum() / status.size

        conditionStatus = when {
            averageStatus < 1.3 -> ConditionDescriptions.GREAT
            averageStatus in 1.3 .. 2.3 -> ConditionDescriptions.DECENT
            else -> ConditionDescriptions.POOR
        }
        return conditionStatus.description
    }

}