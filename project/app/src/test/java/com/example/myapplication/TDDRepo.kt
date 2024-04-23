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
            location = greatConditionsWithAlert.location,
            windSpeed = greatConditionsWithAlert.windSpeed,
            windGust = greatConditionsWithAlert.windGust,
            windDir = greatConditionsWithAlert.windDir,
            waveHeight = greatConditionsWithAlert.waveHeight,
            waveDir = greatConditionsWithAlert.waveDir,
            wavePeriod = greatConditionsWithAlert.wavePeriod,
            alerts = greatConditionsWithAlert.alerts,
        )
        assert(status == ConditionStatus.POOR) {"Status should be Dårlig but was $status"}
    }

    @Test
    fun conditionsAreDecentWhenStateIsConsideredDecent() {
        val status =  SmackLipRepositoryImpl().getConditionStatus(
            location = decentConditionsHoddevik.location,
            windSpeed = decentConditionsHoddevik.windSpeed,
            windGust = decentConditionsHoddevik.windGust,
            windDir = decentConditionsHoddevik.windDir,
            waveHeight = decentConditionsHoddevik.waveHeight,
            waveDir = decentConditionsHoddevik.waveDir,
            wavePeriod = decentConditionsHoddevik.wavePeriod,
            alerts = decentConditionsHoddevik.alerts,
        )
        assert(status == ConditionStatus.DECENT) {"Status should be Greit but was $status"}
    }

    @Test
    fun conditionsArePoorWhenStateIsSplit() {
        // while some conditions are great, it should be poor if the rest are bad

        val status = SmackLipRepositoryImpl().getConditionStatus(
            location =  splitConditions.location,
            windSpeed = splitConditions.windSpeed,
            windGust = splitConditions.windGust,
            windDir = splitConditions.windDir,
            waveHeight = splitConditions.waveHeight,
            waveDir = splitConditions.waveDir,
            wavePeriod = splitConditions.wavePeriod,
            alerts = splitConditions.alerts,
        )
        assert(status == ConditionStatus.POOR) { "Status should be Dårlig but was $status" }
    }
    @Test
    fun conditionsAreGreatWhenConsideredGreat() {
        val status = SmackLipRepositoryImpl().getConditionStatus(
            location = greatConditionsWithoutAlert.location,
            windSpeed =  greatConditionsWithoutAlert.windSpeed,
            windGust =  greatConditionsWithoutAlert.windGust,
            windDir =  greatConditionsWithoutAlert.windDir,
            waveHeight = greatConditionsWithoutAlert.waveHeight,
            waveDir =  greatConditionsWithoutAlert.waveDir,
            wavePeriod =  greatConditionsWithoutAlert.wavePeriod,
            alerts = greatConditionsWithoutAlert.alerts
        )
        assert(status == ConditionStatus.GREAT) {"Status should be Utmerket but was $status"}
    }
}