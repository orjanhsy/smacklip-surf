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
}

class OceanforecastRepositoryImpl(
    private val dataSource: OceanforecastDataSource = OceanforecastDataSource()
): OceanforecastRepository {

    override suspend fun getTimeSeries(surfArea: SurfArea): List<Pair<String, DataOF>> {
        //gets list of timeseries objects, containing time and data
        val timeSeries: List<TimeserieOF> = try { dataSource.fetchOceanforecast(surfArea).properties.timeseries }
        catch (e: Exception) {
            listOf()
        }

        //returns a list of time mapped to data
        return timeSeries.map { it.time to it.data }
    }
}