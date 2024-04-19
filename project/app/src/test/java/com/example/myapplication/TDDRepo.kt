package com.example.myapplication

import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.conditions.ConditionDescriptions
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.surfarea.SurfAreaScreenViewModel
import org.junit.Test
import java.util.concurrent.locks.Condition

class TDDRepo {

    //  repository: smacklip
    private val repo: SmackLipRepository = SmackLipRepositoryImpl()

    // vm: SurfAreaScreen
    private val vm = SurfAreaScreenViewModel()


    object greatConditionsWithAlert {
        val location = SurfArea.HODDEVIK
        val windSpeed = 2.0
        val windGust = 2.5
        val windDir = location.optimalWindDir
        val waveHeight = 2.0
        val waveDir = location.optimalWaveDir
        val wavePeriod = 10.0
        val alerts: List<Features> = listOf(Features())
    }

    object greatConditionsWithoutAlert {
        val location = SurfArea.HODDEVIK
        val windSpeed = 2.0
        val windGust = 3.0
        val windDir = location.optimalWindDir
        val waveHeight = 2.0
        val waveDir = location.optimalWaveDir
        val wavePeriod = 12.0
        val alerts: List<Features> = emptyList()
    }

    object decentConditionsHoddevik {
        val location = SurfArea.HODDEVIK
        val windSpeed = 6.0
        val windGust = 10.0
        val windDir = location.optimalWindDir
        val waveHeight = 3.0
        val waveDir = location.optimalWaveDir - 35 % 360
        val wavePeriod = 9.0
        val alerts: List<Features> = emptyList()
    }
    object splitConditions {
        val location = SurfArea.HODDEVIK
        val windSpeed = 12.0 //tbd
        val windGust = 12.0 //tbd
        val windDir = SurfArea.HODDEVIK.optimalWindDir - 90 % 360
        val waveHeight = 2.0
        val waveDir = SurfArea.HODDEVIK.optimalWaveDir
        val wavePeriod = 10.6
        val alerts: List<Features> = emptyList()
    }



    @Test
    fun conditionsArePoorIfAlertsArePresent() {
        assert(
            SmackLipRepositoryImpl().getConditionStatus(
                greatConditionsWithAlert.windSpeed,
                greatConditionsWithAlert.windDir,
                greatConditionsWithAlert.windGust,
                greatConditionsWithAlert.waveHeight,
                greatConditionsWithAlert.waveDir,
                greatConditionsWithAlert.wavePeriod,
                greatConditionsWithAlert.alerts,
            ) == ConditionDescriptions.POOR.description
        )
    }

    @Test
    fun conditionsAreDecentWhenStateIsConsideredDecent() {
        assert(
            SmackLipRepositoryImpl().getConditionStatus(
                decentConditionsHoddevik.windSpeed,
                decentConditionsHoddevik.windDir,
                decentConditionsHoddevik.windGust,
                decentConditionsHoddevik.waveHeight,
                decentConditionsHoddevik.waveDir,
                decentConditionsHoddevik.wavePeriod,
                decentConditionsHoddevik.alerts,
            ) == ConditionDescriptions.DECENT.description
        )
    }
    @Test
    fun conditionsArePoorWhenStateIsSplit() {
        // while some conditions are great, it should be poor if the rest are bad
        assert(
            SmackLipRepositoryImpl().getConditionStatus(
                splitConditions.windSpeed,
                splitConditions.windDir,
                splitConditions.windGust,
                splitConditions.waveHeight,
                splitConditions.waveDir,
                splitConditions.wavePeriod,
                splitConditions.alerts,
            ) == ConditionDescriptions.POOR.description
        )
    }
    @Test
    fun conditionsAreGreatWhenConsideredGreat() {
        // while some conditions are great, it should be poor if the rest are bad
        assert(
            SmackLipRepositoryImpl().getConditionStatus(
                greatConditionsWithoutAlert.windSpeed,
                greatConditionsWithoutAlert.windDir,
                greatConditionsWithoutAlert.windGust,
                greatConditionsWithoutAlert.waveHeight,
                greatConditionsWithoutAlert.waveDir,
                greatConditionsWithoutAlert.wavePeriod,
                greatConditionsWithoutAlert.alerts,
            ) == ConditionDescriptions.GREAT.description
        )
    }

    @Test
    fun vmConditionStatusEqualsRepoImplementation() {
        assert(
            vm.getConditionStatus(
                decentConditionsHoddevik.windSpeed,
                decentConditionsHoddevik.windDir,
                decentConditionsHoddevik.windGust,
                decentConditionsHoddevik.waveHeight,
                decentConditionsHoddevik.waveDir,
                decentConditionsHoddevik.wavePeriod,
                decentConditionsHoddevik.alerts
            ) == repo.getConditionStatus(
                decentConditionsHoddevik.windSpeed,
                decentConditionsHoddevik.windDir,
                decentConditionsHoddevik.windGust,
                decentConditionsHoddevik.waveHeight,
                decentConditionsHoddevik.waveDir,
                decentConditionsHoddevik.wavePeriod,
                decentConditionsHoddevik.alerts
            )
        )
    }
}