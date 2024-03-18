package com.example.myapplication.data.oceanforecast

import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF

interface OceanforecastRepository{
    suspend fun getTimeSeries(): List<Pair<String, DataOF>>
    suspend fun getWaveHeights(): List<Pair<String, Double>>
}

class OceanforecastRepositoryImpl(
    private val dataSource: OceanforecastDataSource = OceanforecastDataSource()
): OceanforecastRepository {
    //vet ikke hva som er best practice: ha datasource som argument eller ha det inni klassen

    override suspend fun getTimeSeries(): List<Pair<String, DataOF>> {
        //henter timeSeries som er en liste av TimeSerie-objekter som består av de to variablene time og data
        val timeSeries : List<TimeserieOF> = dataSource.fetchOceanforecast().properties.timeseries;

        //returnerer en map som mapper time til data, dermed ser man data for hver tidspunkt
        return timeSeries.map {it.time to it.data}
    }


    private fun findWaveHeightFromData(dataOF: DataOF): Double {
        return dataOF.instant.details.sea_surface_wave_height
    }


    override suspend fun getWaveHeights(): List<Pair<String, Double>> {
        val timeSeries = getTimeSeries();
        return timeSeries.map { it.first to findWaveHeightFromData(it.second) }

    }


}