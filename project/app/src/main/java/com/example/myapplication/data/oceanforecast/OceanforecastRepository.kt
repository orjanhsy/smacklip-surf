package com.example.myapplication.data.oceanforecast

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea
import java.time.DayOfWeek
import java.time.LocalDate

interface OceanforecastRepository{
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>>
    suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<String, Double>>

    suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>>
}

class OceanforecastRepositoryImpl(
    private val dataSource: OceanforecastDataSource = OceanforecastDataSource()
): OceanforecastRepository {
    //vet ikke hva som er best practice: ha datasource som argument eller ha det inni klassen

    private var timeSeries: List<Pair<String, DataOF>> = emptyList()

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>> {
        //henter timeSeries som er en liste av TimeSerie-objekter som består av de to variablene time og data
        val timeSeries: List<TimeserieOF> =
            dataSource.fetchOceanforecast(surfArea).properties.timeseries

        //returnerer en map som mapper time til data, dermed ser man data for hver tidspunkt
        return timeSeries.map { it.time to it.data }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getTimeSeriesDayByDay(surfArea: SurfArea): List<List<Pair<String, DataOF>>> {
        //henter timeSeries som er en liste av TimeSerie-objekter som består av de to variablene time og data
        val timeSeries: List<TimeserieOF> =
            dataSource.fetchOceanforecast(surfArea).properties.timeseries

        // grupperer dag for dag
        val groupedData = timeSeries.groupBy { LocalDate.parse(it.time).dayOfWeek }
//lager 7 lister for 7 dager
        val resultList = mutableListOf<List<Pair<String, DataOF>>>()

        // Legger til data for hver dag i listen
        for (dayOfWeek in DayOfWeek.values()) {
            val dayData = groupedData[dayOfWeek] ?: emptyList()
            resultList.add(dayData.map { it.time to it.data })
        }

        return resultList
    }


    private fun findWaveHeightFromData(dataOF: DataOF): Double {
        return dataOF.instant.details.sea_surface_wave_height
    }


    override suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<String, Double>> {
        // Hent timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = getTimeSeries(surfArea)
        // Map og konverter timeSeries-dataene til bølgehøyder
        return timeSeriesForArea.map { it.first to findWaveHeightFromData(it.second) }
    }
}