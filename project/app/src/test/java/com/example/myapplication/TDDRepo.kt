package com.example.myapplication

import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.surfarea.SurfAreaScreenViewModel
import org.junit.Test

class TDDRepo {

    //  repository: smacklip
    private val repo: SmackLipRepository = SmackLipRepositoryImpl()

    // VM: surfAreaScreenVM
    private val vm = SurfAreaScreenViewModel()


    // ------ repo function

    object greatConditionsWithAlert {
        val windSpeed = 4.0
        val windGust = 5.0
        val windDIr = 100.0
        val waveHeight = 4.0
        val waveDir = 100.0
        val wavePeriod = 10.0
        val alerts = listOf(Features())
    }

    object greatConditionsWithoutAlert {
        val windSpeed = 4.0
        val windGust = 5.0
        val windDIr = 100.0
        val waveHeight = 4.0
        val waveDir = 100.0
        val wavePeriod = 10.0
        val alerts: List<Any> = emptyList()
    }

    object decentConditions {
        val windSpeed = 12.0
        val windGust = 17.0
        val windDir = 120.0
        val waveHeight = 4.0
        val waveDir = 100.0
        val wavePeriod = 8.0
        val alerts: List<Any> = emptyList()
    }

    @Test
    fun conditionsArePoorIfAlertsArePresent() {
        SurfArea.entries.forEach {
            vm.updateLocation(it)
            vm.updateAlerts()
            val area = vm.surfAreaScreenUiState.value
            if (area.alerts.isNotEmpty()) {

            }
        }
    }
}