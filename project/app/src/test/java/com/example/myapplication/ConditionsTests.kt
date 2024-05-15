package com.example.myapplication


import com.example.myapplication.model.conditions.ConditionStatus
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.utils.ConditionUtils
import org.junit.Test

class ConditionsTests {



    // vm: SurfAreaScreen
    private val conditionsUtils = ConditionUtils()


    private object greatConditionsWithoutAlert {
        val location = SurfArea.HODDEVIK
        val windSpeed = 2.0
        val windDir = location.optimalWindDir
        val waveHeight = 2.0
        val waveDir = location.optimalWaveDir
        val wavePeriod = 12.0
    }
    private object greatConditionsWithoutAlertOpositWind {
        val location = SurfArea.HODDEVIK
        val windSpeed = 2.0
        val windDir = (location.optimalWindDir + 180) % 360
        val waveHeight = 2.0
        val waveDir = location.optimalWaveDir
        val wavePeriod = 12.0
    }

    private object decentConditionsHoddevik {
        val location = SurfArea.HODDEVIK
        val windSpeed = 6.0
        val windDir = location.optimalWindDir
        val waveHeight = 3.0
        val waveDir = location.optimalWaveDir - 25 % 360
        val wavePeriod = 9.0
    }
    private object splitConditions {
        val location = SurfArea.HODDEVIK
        val windSpeed = 15.0
        val windDir = SurfArea.HODDEVIK.optimalWindDir - 90 % 360
        val waveHeight = 2.0
        val waveDir = SurfArea.HODDEVIK.optimalWaveDir - 45 % 360
        val wavePeriod = 10.6
    }



    @Test
    fun conditionsAreDecentWhenStateIsConsideredDecent() {
        val status =  conditionsUtils.getConditionStatus(
            location = decentConditionsHoddevik.location,
            windSpeed = decentConditionsHoddevik.windSpeed,
            windDir = decentConditionsHoddevik.windDir,
            waveHeight = decentConditionsHoddevik.waveHeight,
            waveDir = decentConditionsHoddevik.waveDir,
            wavePeriod = decentConditionsHoddevik.wavePeriod,
        )
        assert(status == ConditionStatus.DECENT) {"Status should be Greit but was $status"}
    }

    @Test
    fun conditionsArePoorWhenStateIsSplit() {
        // while some conditions are great, it should be poor if the rest are bad

        val status = conditionsUtils.getConditionStatus(
            location =  splitConditions.location,
            windSpeed = splitConditions.windSpeed,
            windDir = splitConditions.windDir,
            waveHeight = splitConditions.waveHeight,
            waveDir = splitConditions.waveDir,
            wavePeriod = splitConditions.wavePeriod,
        )
        assert(status == ConditionStatus.POOR) { "Status should be Dårlig but was $status" }
    }
    @Test
    fun conditionsAreGreatWhenConsideredGreat() {
        val status = conditionsUtils.getConditionStatus(
            location = greatConditionsWithoutAlert.location,
            windSpeed =  greatConditionsWithoutAlert.windSpeed,
            windDir =  greatConditionsWithoutAlert.windDir,
            waveHeight = greatConditionsWithoutAlert.waveHeight,
            waveDir =  greatConditionsWithoutAlert.waveDir,
            wavePeriod =  greatConditionsWithoutAlert.wavePeriod,
        )
        assert(status == ConditionStatus.GREAT) {"Status should be Utmerket but was $status"}
    }
    @Test
    fun conditionsAreGreatWithOppositeWind() {
        val status = conditionsUtils.getConditionStatus(
            location = greatConditionsWithoutAlertOpositWind.location,
            windSpeed = greatConditionsWithoutAlertOpositWind.windSpeed,
            windDir = greatConditionsWithoutAlertOpositWind.windDir,
            waveHeight = greatConditionsWithoutAlertOpositWind.waveHeight,
            waveDir = greatConditionsWithoutAlertOpositWind.waveDir,
            wavePeriod = greatConditionsWithoutAlertOpositWind.wavePeriod,
        )
        assert(status == ConditionStatus.GREAT) { "Status should be Utmerket but was $status" }
    }
}