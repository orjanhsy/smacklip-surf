package com.example.myapplication.data.smackLip

import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface Repository {
    val ofLfNext7Days: StateFlow<AllSurfAreasOFLF>
    val wavePeriods: StateFlow<AllWavePeriods>
    val alerts: StateFlow<Map<SurfArea, List<Alert>>>

    suspend fun loadOFlF()
    suspend fun loadWavePeriods()
    suspend fun loadAlerts()
}

class RepositoryImpl(

): Repository {

    private val _ofLfNext7Days: MutableStateFlow<AllSurfAreasOFLF> = MutableStateFlow(AllSurfAreasOFLF())
    override val ofLfNext7Days: AllSurfAreasOFLF
        get() = TODO("Not yet implemented")
    override val wavePeriods: AllWavePeriods
        get() = TODO("Not yet implemented")
    override val alerts: Map<SurfArea, List<Alert>>
        get() = TODO("Not yet implemented")

    override suspend fun loadOFlF() {
        TODO("Not yet implemented")
    }

    override suspend fun loadWavePeriods() {
        TODO("Not yet implemented")
    }

    override suspend fun loadAlerts() {
        TODO("Not yet implemented")
    }
}