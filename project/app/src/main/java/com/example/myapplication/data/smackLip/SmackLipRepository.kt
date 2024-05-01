package com.example.myapplication.data.smackLip

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
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.smacklip.DayData
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.math.abs


interface SmackLipRepository {
    suspend fun getRelevantAlertsFor(surfArea: SurfArea): List<Features>
    suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    suspend fun getWaveDirections(surfArea: SurfArea): List<Pair<List<Int>, Double>>

    suspend fun getTimeSeriesOFLF(surfArea: SurfArea): Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>>

    suspend fun getOFLFOneDay(day: Int, month: Int, timeseries: Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>> ): DayData
    suspend fun getSurfAreaOFLFNext7Days(surfArea: SurfArea): Forecast7DaysOFLF
    suspend fun getWindDirection(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getAirTemperature(surfArea: SurfArea): List<Pair<List<Int>, Double>>
    suspend fun getSymbolCode(surfArea: SurfArea): List<Pair<List<Int>, String>>
    fun getTimeListFromTimeString(timeString : String): List<Int>
    suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>>


    // waveforecast
    suspend fun getAllWavePeriodsNext3Days(): AllWavePeriods
    suspend fun getWavePeriodsNext3DaysForArea(surfArea: SurfArea): List<Double?> // skal fjernes til fordel for AllWavePeriods.wavePeriods[surfArea]

    suspend fun getAllOFLF7Days (): AllSurfAreasOFLF

    fun getConditionStatus(
        location: SurfArea,
        wavePeriod: Double?,
        windSpeed: Double,
        windGust: Double,
        windDir: Double,
        waveHeight: Double,
        waveDir: Double,
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
    : DayData {
        //henter data for den spesifikke dagen fra OF og LF
        val OFmap: List<Pair<String, DataOF>>? = timeseries.first[day]
        val LFmap: List<Pair<String, DataLF>>? = timeseries.second[day]

        val map : MutableMap<List<Int>, MutableList<Any>> = mutableMapOf()

        LFmap!!.map {
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

        OFmap!!.map {
            val time = getTimeListFromTimeString(it.first)
            try {
                map[time]!!.add(it.second.instant.details.sea_surface_wave_height)
                map[time]!!.add(it.second.instant.details.sea_water_to_direction)
                //symbol_code

            }catch (_: Exception){
            }
        }

        val filteredMap = map.filter {
            it.value.size == 7
        }

        // [windSpeed, windSpeedOfGust, windDirection, airTemperature, symbolCode, Waveheight, waveDirection]
        val dayData= DayData(
            filteredMap.entries.associate { (time, data) ->
                time to DataAtTime(
                    windSpeed = data[0] as Double,
                    windGust = data[1] as Double,
                    windDir = data[2] as Double,
                    airTemp = data[3] as Double,
                    symbolCode = data[4] as String,
                    waveHeight = data[5] as Double,
                    waveDir = data[6] as Double
                )
            }
        )
        return dayData
    }

    fun nDaysInMonth(month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> 28 // Anta at det ikke er et skuddår for enkelhets skyld
            else -> throw IllegalArgumentException("Ugyldig månedsnummer")
        }
    }

    override suspend fun getSurfAreaOFLFNext7Days(surfArea: SurfArea): Forecast7DaysOFLF {
        val timeseries:  Pair<Map<Int, List<Pair<String, DataOF>>>, Map<Int, List<Pair<String, DataLF>>>> = getTimeSeriesOFLF(surfArea = surfArea)
        val time = getTimeListFromTimeString(oceanForecastRepository.getTimeSeries(surfArea)[0].first)
        val day = time[2]
        val month = time[1]

        val forecastNext7Days: MutableList<DayData> = mutableListOf()

        for (i in day .. (day + 6)) {
            val daysInMonth = nDaysInMonth(month)
            var actualDay = i
            if (daysInMonth < i) {
                actualDay -= daysInMonth
            }
            forecastNext7Days.add(getOFLFOneDay(actualDay, month, Pair(timeseries.first, timeseries.second)))
        }

        return Forecast7DaysOFLF(forecastNext7Days)
    }


    override suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>> {
        return oceanForecastRepository.getTimeSeriesDayByDay(surfArea)
    }

    override suspend fun getAllWavePeriodsNext3Days(): AllWavePeriods {
        return waveForecastRepository.allRelevantWavePeriodsNext3DaysHardCoded()
    }

    override suspend fun getWavePeriodsNext3DaysForArea(surfArea: SurfArea): List<Double?> {
        val wavePeriods = waveForecastRepository.wavePeriodsNext3DaysForArea(surfArea.modelName, surfArea.pointId)

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
        windSpeed: Double,
        windGust: Double,
        windDir: Double,
        waveHeight: Double,
        waveDir: Double,
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
    override suspend fun getAllOFLF7Days (): AllSurfAreasOFLF {

        return coroutineScope {
            val res = SurfArea.entries.associateWith {
                async { getSurfAreaOFLFNext7Days(it) }
            }

            val newRes = res.entries.associate{
                it.key to it.value.await()
            }

            AllSurfAreasOFLF(
                next7Days = newRes
            )
        }

    }

}