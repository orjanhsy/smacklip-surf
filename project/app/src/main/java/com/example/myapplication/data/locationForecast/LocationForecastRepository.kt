package com.example.myapplication.data.locationForecast

import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.locationforecast.DataLF
import com.example.myapplication.model.locationforecast.TimeserieLF

interface LocationForecastRepository {
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>>
    suspend fun getWindDirection(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getTemperature(surfArea: SurfArea): List<Pair<String, Double>>
    suspend fun getSymbolCodeNextOneHour(surfArea: SurfArea): List<Pair<String, String>>
    suspend fun getSymbolCodeNextSixHours(surfArea: SurfArea): List<Pair<String, String>>

   /* suspend fun getSymbolCodeNextTwelveHours(surfArea: SurfArea): List<Pair<String, String>>*/


}

class LocationForecastRepositoryImpl(
    private val locationForecastDataSource: LocationForecastDataSource = LocationForecastDataSource()
): LocationForecastRepository {

    //returnerer en liste med par av tidspunkt og data tilhørende dette tidspunktet
    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataLF>> {
        val timeSeries : List<TimeserieLF> = locationForecastDataSource.fetchLocationForecastData(surfArea).properties.timeseries
        return timeSeries.map { it.time to it.data }
    }

    private fun findWindSpeedFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_speed
    }

    private fun findWindSpeedOfGustFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_speed_of_gust
    }

    private fun findWindDirectionFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.wind_from_direction
    }

    private fun findTemperatureFromData(dataLF: DataLF): Double{
        return dataLF.instant.details.air_temperature
    }

    //Det er ikke alltif next_1_hours eller next_six_hours i en DataLF
    //returnerer null hvis NullPointerException
    private fun findSymbolCodeOneHourFromData(dataLF: DataLF): String? {
        return try {
            dataLF.next_1_hours.summary.symbol_code
        }catch (_: NullPointerException){
            null
        }
    }

    private fun findSymbolCodeSixHoursFromData(dataLF: DataLF): String? {
        return try {
            dataLF.next_6_hours.summary.symbol_code
        }catch (_: NullPointerException){
            null
        }
    }
    private fun findWeatherIconTwelveHoursFromData(dataLF: DataLF): String {
        return dataLF.next_12_hours.summary.symbol_code
    }


    override suspend fun getWindDirection(surfArea: SurfArea): List<Pair<String, Double>> {
        // Henter timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til vindretning
        return timeSeriesForArea.map {it.first to findWindDirectionFromData(it.second)} ?: emptyList()

    }

    override suspend fun getWindSpeed(surfArea: SurfArea): List<Pair<String, Double>> {
        // Henter timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til vindhastighet
        return timeSeriesForArea.map {it.first to findWindSpeedFromData(it.second)} ?: emptyList()


    }

    override suspend fun getWindSpeedOfGust(surfArea: SurfArea): List<Pair<String, Double>> {
        // Henter timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til vindretning
        return timeSeriesForArea.map {it.first to findWindSpeedOfGustFromData(it.second)} ?: emptyList()

    }

    override suspend fun getTemperature(surfArea: SurfArea): List<Pair<String, Double>> {
        val timeSeriesForArea = getTimeSeries(surfArea)
        return timeSeriesForArea.map { it.first to findTemperatureFromData(it.second)} ?: emptyList()
    }

    override suspend fun getSymbolCodeNextOneHour(surfArea: SurfArea): List<Pair<String, String>> {
        val timeSeriesForArea = getTimeSeries(surfArea)
        return timeSeriesForArea.mapNotNull {
            val symbolCode = findSymbolCodeOneHourFromData(it.second)
            if (symbolCode != null) {
                it.first to symbolCode
            } else {
                null
            }
        }
    }


    override suspend fun getSymbolCodeNextSixHours(surfArea: SurfArea): List<Pair<String, String>> {
        val timeSeriesForArea = getTimeSeries(surfArea)
        return timeSeriesForArea.mapNotNull {
            val symbolCode = findSymbolCodeSixHoursFromData(it.second)
            if (symbolCode != null) {
                it.first to symbolCode
            } else {
                null
            }
        }
    }


    /* Denne er kanskje ikke relevant? lar den bli i tilfelle
    override suspend fun getSymbolCodeNextTwelveHours(surfArea: SurfArea): List<Pair<String, String>> {
        val timeSeriesForArea = getTimeSeries(surfArea)
        return timeSeriesForArea.map { it.first to findWeatherIconTwelveHoursFromData(it.second)} ?: emptyList()
    }
     */

}
