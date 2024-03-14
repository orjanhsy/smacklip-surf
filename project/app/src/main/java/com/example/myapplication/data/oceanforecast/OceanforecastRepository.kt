package com.example.myapplication.data.oceanforecast

import com.example.myapplication.model.oceanforecast.DataOF
import com.example.myapplication.model.oceanforecast.TimeserieOF

class OceanforecastRepository(private val dataSource: OceanforecastDataSource) {
    //vet ikke hva som er best practice: ha datasource som argument eller ha det inni klassen

    suspend fun getTimeSeries(): List<Pair<String, DataOF>> {
        //henter timeSeries som er en liste av TimeSerie-objekter som består av de to variablene time og data
        val timeSeries : List<TimeserieOF> = dataSource.fetchOceanforecast().properties.timeseries;

        //returnerer en map som mapper time til data, dermed ser man data for hver tidspunkt
        return timeSeries.map {it.time to it.data}
    }


    private fun findWaveHeightFromData(dataOF: DataOF): Double {
        return dataOF.instant.details.sea_surface_wave_height
    }

    suspend fun getWaveHeights(timeSeries: List<Pair<String, DataOF>>): List<Pair<String, Double>> {
        //val timeSeries = getTimeSeries();
        return timeSeries.map { it.first to findWaveHeightFromData(it.second) }

    }


}