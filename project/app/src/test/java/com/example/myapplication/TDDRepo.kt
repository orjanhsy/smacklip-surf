package com.example.myapplication

import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.surfarea.SurfAreaScreenViewModel
import org.junit.Test

class TDDRepo {

    //  repository: smacklip
    private val repo: SmackLipRepository = SmackLipRepositoryImpl()

    // vm: SurfAreaScreen
    private val vm = SurfAreaScreenViewModel()


    private object greatConditionsWithAlert {
        val location = SurfArea.HODDEVIK
        val windSpeed = 2.0
        val windGust = 2.5
        val windDir = location.optimalWindDir
        val waveHeight = 2.0
        val waveDir = location.optimalWaveDir
        val wavePeriod = 10.0
        val alerts: List<Features> = listOf(Features())
    }

    private object greatConditionsWithoutAlert {
        val location = SurfArea.HODDEVIK
        val windSpeed = 2.0
        val windGust = 3.0
        val windDir = location.optimalWindDir
        val waveHeight = 2.0
        val waveDir = location.optimalWaveDir
        val wavePeriod = 12.0
        val alerts: List<Features> = emptyList()
    }

    private object decentConditionsHoddevik {
        val location = SurfArea.HODDEVIK
        val windSpeed = 6.0
        val windGust = 8.0
        val windDir = location.optimalWindDir
        val waveHeight = 3.0
        val waveDir = location.optimalWaveDir - 25 % 360
        val wavePeriod = 9.0
        val alerts: List<Features> = emptyList()
    }
    private object splitConditions {
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
        val status = SmackLipRepositoryImpl().getConditionStatus(
            greatConditionsWithAlert.location,
            greatConditionsWithAlert.windSpeed,
            greatConditionsWithAlert.windGust,
            greatConditionsWithAlert.windDir,
            greatConditionsWithAlert.waveHeight,
            greatConditionsWithAlert.waveDir,
            greatConditionsWithAlert.wavePeriod,
            greatConditionsWithAlert.alerts,
        )
        assert(status == ConditionStatus.POOR.description) {"Status should be Dårlig but was $status"}
    }

    @Test
    fun conditionsAreDecentWhenStateIsConsideredDecent() {
        val status =  SmackLipRepositoryImpl().getConditionStatus(
            decentConditionsHoddevik.location,
            decentConditionsHoddevik.windSpeed,
            decentConditionsHoddevik.windGust,
            decentConditionsHoddevik.windDir,
            decentConditionsHoddevik.waveHeight,
            decentConditionsHoddevik.waveDir,
            decentConditionsHoddevik.wavePeriod,
            decentConditionsHoddevik.alerts,
        )
        assert(status == ConditionStatus.DECENT.description) {"Status should be Greit but was $status"}
    }

    @Test
    fun conditionsArePoorWhenStateIsSplit() {
        // while some conditions are great, it should be poor if the rest are bad

        val status = SmackLipRepositoryImpl().getConditionStatus(
            splitConditions.location,
            splitConditions.windSpeed,
            splitConditions.windGust,
            splitConditions.windDir,
            splitConditions.waveHeight,
            splitConditions.waveDir,
            splitConditions.wavePeriod,
            splitConditions.alerts,
        )
        assert(status == ConditionStatus.POOR.description) { "Status should be Dårlig but was $status" }
    }
    @Test
    fun conditionsAreGreatWhenConsideredGreat() {
        val status = SmackLipRepositoryImpl().getConditionStatus(
            greatConditionsWithoutAlert.location,
            greatConditionsWithoutAlert.windSpeed,
            greatConditionsWithoutAlert.windGust,
            greatConditionsWithoutAlert.windDir,
            greatConditionsWithoutAlert.waveHeight,
            greatConditionsWithoutAlert.waveDir,
            greatConditionsWithoutAlert.wavePeriod,
            greatConditionsWithoutAlert.alerts
        )
        assert(status == ConditionStatus.GREAT.description) {"Status should be Utmerket but was $status"}
    }
}