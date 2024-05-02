package com.example.myapplication.data.smackLip

import com.example.myapplication.data.locationForecast.LocationForecastRepository
import com.example.myapplication.data.locationForecast.LocationForecastRepositoryImpl
import com.example.myapplication.data.metalerts.MetAlertsRepository
import com.example.myapplication.data.metalerts.MetAlertsRepositoryImpl
import com.example.myapplication.data.oceanforecast.OceanforecastRepository
import com.example.myapplication.data.oceanforecast.OceanforecastRepositoryImpl
import com.example.myapplication.data.waveforecast.WaveForecastRepository
import com.example.myapplication.data.waveforecast.WaveForecastRepositoryImpl
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface Repository {
    val ofLfNext7Days: StateFlow<AllSurfAreasOFLF>
    val wavePeriods: StateFlow<AllWavePeriods>
    val alerts: StateFlow<Map<SurfArea, List<Alert>>>

    suspend fun loadOFlF()
    suspend fun loadWavePeriods()
    suspend fun loadAlerts()
}

class RepositoryImpl(
    val waveForecastRepository: WaveForecastRepository = WaveForecastRepositoryImpl(),
    val oceanForecastRepository: OceanforecastRepository = OceanforecastRepositoryImpl(),
    val locationForecastRepository: LocationForecastRepository = LocationForecastRepositoryImpl(),
    val metAlertsRepository: MetAlertsRepository = MetAlertsRepositoryImpl()

): Repository {

    init {
        loadOFlF()
        loadWavePeriods()
        loadAlerts()
    }

    private val _ofLfNext7Days: MutableStateFlow<AllSurfAreasOFLF> = MutableStateFlow(AllSurfAreasOFLF())
    private val _wavePeriods: MutableStateFlow<AllWavePeriods> = MutableStateFlow(AllWavePeriods())
    private val _alerts: MutableStateFlow<Map<SurfArea, List<Alert>>> = MutableStateFlow(mapOf())

    override val ofLfNext7Days: StateFlow<AllSurfAreasOFLF> = _ofLfNext7Days.asStateFlow()
    override val wavePeriods: StateFlow<AllWavePeriods> = _wavePeriods.asStateFlow()
    override val alerts: StateFlow<Map<SurfArea, List<Alert>>> = _alerts.asStateFlow()

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