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
        val windSpeed = 4.0
        val windGust = 5.0
        val windDir = 100.0
        val waveHeight = 4.0
        val waveDir = 100.0
        val wavePeriod = 10.0
        val alerts: List<Features> = listOf(Features())
    }

    object greatConditionsWithoutAlert {
        val windSpeed = 4.0
        val windGust = 5.0
        val windDir = 100.0
        val waveHeight = 4.0
        val waveDir = 100.0
        val wavePeriod = 10.0
        val alerts: List<Features> = emptyList()
    }

    object decentConditionsHoddevik {
        val windSpeed = 8.0 //tbd
        val windGust = 10.0 //tbd
        val windDir = SurfArea.HODDEVIK.optimalDirection - 45
        val waveHeight = 3.0
        val waveDir = SurfArea.HODDEVIK.optimalDirection - 45
        val wavePeriod = 8.5
        val alerts: List<Features> = emptyList()
    }
    object splitConditions {
        val windSpeed = 17.0 //tbd
        val windGust = 17.0 //tbd
        val windDir = SurfArea.HODDEVIK.optimalDirection - 45
        val waveHeight = 2.0
        val waveDir = SurfArea.HODDEVIK.optimalDirection - 45
        val wavePeriod = 9.5
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