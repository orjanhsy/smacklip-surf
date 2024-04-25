package com.example.myapplication.data.smackLip

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.conditions.Conditions
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs


interface SmackLipRepository {
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features>
    suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    suspend fun getWaveDirections(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    suspend fun getTimeSeriesOFLF(surfArea: SurfArea): Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>>
    suspend fun getOFLFOneDay(day: Int, month: Int, timeseries: Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>> ): Map<List<Int>, List<Any>>
    suspend fun getOFLFDataNext7Days(surfArea: SurfArea): List<Map<List<Int>, List<Any>>>
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

    suspend fun asyncCalls (): Map<SurfArea, List<Map<List<Int>, List<Any>>>>

    fun getConditionStatus(
        location: SurfArea,
        wavePeriod: Double?,
        waveHeight: Double,
        waveDir: Double,
        windDir: Double,
        windSpeed: Double,
        windGust: Double,
        alerts: List<Features>
    ): ConditionStatus

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

    override suspend fun getTimeSeriesOFLF(surfArea: SurfArea): Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>> {
        //Par med <timeserie for OF, så timeserie LF>
        //OF er lenger enn LF
        //en timeserie er et tidsintervall med data, nærmeste dager er det hver time, deretter hver 6. time
        val timeSeries = Pair(oceanForecastRepository.getTimeSeries(surfArea), locationForecastRepository.getTimeSeries(surfArea))
        val timeSeriesMapOF: MutableMap<Int, MutableList<Pair<String, DataOF>>> = mutableMapOf()
        val timeSeriesMapLF: MutableMap<Int, MutableList<Pair<String, DataLF>>> = mutableMapOf()


        val ofMapped = timeSeries.first.map {
            val timeStamp = getTimeListFromTimeString(it.first)
            try {timeSeriesMapOF[timeStamp[2]]!!.add(it)}
            catch(e: Exception) {
                timeSeriesMapOF[timeStamp[2]] = mutableListOf(it)
            }
        }

        val lfMapped = timeSeries.second.map {
            val timeStamp = getTimeListFromTimeString(it.first)
            try {timeSeriesMapLF[timeStamp[2]]!!.add(it)}
            catch(e: Exception) {

                timeSeriesMapLF[timeStamp[2]] = mutableListOf(it)
            }
        }

        return Pair(timeSeriesMapOF, timeSeriesMapLF)

    }
    // returnerer map<tidspunkt-> [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]>
    override suspend fun getOFLFOneDay(day: Int, month: Int, timeseries: Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>> )
    : Map<List<Int>, List<Any>>{
        //henter data for den spesifikke dagen fra OF og LF
        val OFmap: List<Pair<String, DataOF>>? = timeseries.first[day]
        val LFmap: List<Pair<String, DataLF>>? = timeseries.second[day]

        val map : MutableMap<List<Int>, MutableList<Any>> = mutableMapOf()

        LFmap?.map {
            val time = getTimeListFromTimeString(it.first)
            try {
                map[time]!!.add(it.second.instant.details.wind_speed)
                map[time]!!.add(it.second.instant.details.wind_speed_of_gust)
                map[time]!!.add(it.second.instant.details.wind_from_direction)
                map[time]!!.add(it.second.instant.details.air_temperature)
                try {
                    map[time]!!.add(it.second.next_1_hours.summary.symbol_code)
                } catch (e: NullPointerException){
                    map[time]!!.add(it.second.next_6_hours.summary.symbol_code)
                }

                //symbol_code

            }catch (e: Exception){
                map[time] = mutableListOf()
                map[time]!!.add(it.second.instant.details.wind_speed)
                map[time]!!.add(it.second.instant.details.wind_speed_of_gust)
                map[time]!!.add(it.second.instant.details.wind_from_direction)
                map[time]!!.add(it.second.instant.details.air_temperature)
                try {
                    map[time]!!.add(it.second.next_1_hours.summary.symbol_code)
                } catch (e: NullPointerException){
                    map[time]!!.add(it.second.next_6_hours.summary.symbol_code)
                }

            }
        }

        OFmap?.map {
            val time = getTimeListFromTimeString(it.first)
            try {
                map[time]!!.add(it.second.instant.details.sea_surface_wave_height)
                map[time]!!.add(it.second.instant.details.sea_water_to_direction)

                //symbol_code

            }catch (_: Exception){

            }
        }
/*
        map.filter {
            it.value.size == 7
        }

 */

        return map

    }

    suspend fun nDaysInMonth(month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> 28 // Anta at det ikke er et skuddår for enkelhets skyld
            else -> throw IllegalArgumentException("Ugyldig månedsnummer")
        }
    }

    override suspend fun getOFLFDataNext7Days(surfArea: SurfArea): List<Map<List<Int>, List<Any>>> {
        val timeseries:  Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>> = getTimeSeriesOFLF(surfArea = surfArea)
        val time = getTimeListFromTimeString(oceanForecastRepository.getTimeSeries(surfArea)[0].first)
        val day = time[2]
        val month = time[1]

        val forecastNext7Days: MutableList<Map<List<Int>, List<Any>>> = mutableListOf()

        for (i in day .. (day + 6)) {
            val daysInMonth = nDaysInMonth(month)
            var actualDay = i
            if (daysInMonth < i) {
                actualDay -= daysInMonth
            }
            forecastNext7Days.add(getOFLFOneDay(actualDay, month, Pair(timeseries.first, timeseries.second)))
        }
        return forecastNext7Days
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
        Log.d("SmackLipDataOneDay", "Getting data for one day")
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
            Log.d("GetDataSmackLip", "Updating data for 7 days ")
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
        wavePeriod: Double?,
        waveHeight: Double,
        waveDir: Double,
        windDir: Double,
        windSpeed: Double,
        windGust: Double,
        alerts: List<Features>
    ): ConditionStatus {
        var conditionStatus: ConditionStatus = ConditionStatus.DECENT

        if (wavePeriod == null) {
            return ConditionStatus.BLANK
        }
        // conditions that result in poor status regardless of other variables.
        if (
            windSpeed >= Conditions.WIND_SPEED_UPPER_BOUND.value
            || waveHeight <= Conditions.WAVE_HEIGHT_LOWER_BOUND.value
            || waveHeight >= Conditions.WAVE_HEIGHT_UPPER_BOUND.value
            || wavePeriod <= Conditions.WAVE_PERIOD_LOWER_BOUND.value
            || alerts.isNotEmpty()
        ) {
            conditionStatus = ConditionStatus.POOR
            return conditionStatus
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
            averageStatus < 1.3 -> ConditionStatus.GREAT
            averageStatus in 1.3 .. 2.3 -> ConditionStatus.DECENT
            else -> ConditionStatus.POOR
        }
        return conditionStatus
    }

    // testing
    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun asyncCalls (): Map<SurfArea, List<Map<List<Int>, List<Any>>>> {

        return coroutineScope {

            val res = SurfArea.entries.associateWith {
                async { getOFLFDataNext7Days(it) }
            }

            res.forEach{
                it.value.await()
            }

            res
        }

    }

}