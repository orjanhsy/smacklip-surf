package com.example.myapplication.data.oceanforecast

import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF
import com.example.myapplication.model.surfareas.SurfArea

interface OceanforecastRepository{
    suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>>
    suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<String, Double>>
}

class OceanforecastRepositoryImpl(
    private val dataSource: OceanforecastDataSource = OceanforecastDataSource()
): OceanforecastRepository {
    //vet ikke hva som er best practice: ha datasource som argument eller ha det inni klassen

    private var timeSeries : List<Pair<String, DataOF>> = emptyList()

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>> {
        //henter timeSeries som er en liste av TimeSerie-objekter som består av de to variablene time og data
        val timeSeries : List<TimeserieOF> = dataSource.fetchOceanforecast(surfArea).properties.timeseries;

        //returnerer en map som mapper time til data, dermed ser man data for hver tidspunkt
        return timeSeries.map {it.time to it.data}
    }


    private fun findWaveHeightFromData(dataOF: DataOF): Double {
        return dataOF.instant.details.sea_surface_wave_height
    }


    override suspend fun getWaveHeights(surfArea: SurfArea): List<Pair<String, Double>> {
        // Hent alle timeSeries for alle surfArea-områder
        val allTimeSeries = SurfArea.entries.associateWith { getTimeSeries(it) }
        // Hent timeSeries for det spesifikke surfArea-området
        val timeSeriesForArea = allTimeSeries[surfArea]
        // Map og konverter timeSeries-dataene til bølgehøyder
        return timeSeriesForArea?.map { it.first to findWaveHeightFromData(it.second) } ?: emptyList()

    }


}