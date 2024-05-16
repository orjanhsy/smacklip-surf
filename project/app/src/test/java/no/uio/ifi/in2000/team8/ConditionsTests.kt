package no.uio.ifi.in2000.team8


import no.uio.ifi.in2000.team8.model.conditions.ConditionStatus
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.utils.ConditionUtils
import org.junit.Test

class ConditionsTests {



    // vm: SurfAreaScreen
    private val conditionsUtils = ConditionUtils()


    private object GreatConditionsWithoutAlert {
        val location = SurfArea.HODDEVIK
        const val WIND_SPEED = 2.0
        val windDir = location.optimalWindDir
        const val WAVE_HEIGHT = 2.0
        val waveDir = location.optimalWaveDir
        const val WAVE_PERIOD = 12.0
    }
    private object GreatConditionsWithoutAlertOppositeWind {
        val location = SurfArea.HODDEVIK
        const val WIND_SPEED = 2.0
        val windDir = (location.optimalWindDir + 180) % 360
        const val WAVE_HEIGHT = 2.0
        val waveDir = location.optimalWaveDir
        const val WAVE_PERIOD = 12.0
    }

    private object DecentConditionsHoddevik {
        val location = SurfArea.HODDEVIK
        const val WIND_SPEED = 6.0
        val windDir = location.optimalWindDir
        const val WAVE_HEIGHT = 3.0
        val waveDir = location.optimalWaveDir - 25 % 360
        const val WAVE_PERIOD = 9.0
    }
    private object SplitConditions {
        val location = SurfArea.HODDEVIK
        const val WIND_SPEED = 15.0
        val windDir = SurfArea.HODDEVIK.optimalWindDir - 90 % 360
        const val WAVE_HEIGHT = 2.0
        val waveDir = SurfArea.HODDEVIK.optimalWaveDir - 45 % 360
        const val WAVE_PERIOD = 10.6
    }



    @Test
    fun conditionsAreDecentWhenStateIsConsideredDecent() {
        val status =  conditionsUtils.getConditionStatus(
            location = DecentConditionsHoddevik.location,
            windSpeed = DecentConditionsHoddevik.WIND_SPEED,
            windDir = DecentConditionsHoddevik.windDir,
            waveHeight = DecentConditionsHoddevik.WAVE_HEIGHT,
            waveDir = DecentConditionsHoddevik.waveDir,
            wavePeriod = DecentConditionsHoddevik.WAVE_PERIOD,
        )
        assert(status == ConditionStatus.DECENT) {"Status should be Greit but was $status"}
    }

    @Test
    fun conditionsArePoorWhenStateIsSplit() {
        // while some conditions are great, it should be poor if the rest are bad

        val status = conditionsUtils.getConditionStatus(
            location = SplitConditions.location,
            windSpeed = SplitConditions.WIND_SPEED,
            windDir = SplitConditions.windDir,
            waveHeight = SplitConditions.WAVE_HEIGHT,
            waveDir = SplitConditions.waveDir,
            wavePeriod = SplitConditions.WAVE_PERIOD,
        )
        assert(status == ConditionStatus.POOR) { "Status should be Dårlig but was $status" }
    }
    @Test
    fun conditionsAreGreatWhenConsideredGreat() {
        val status = conditionsUtils.getConditionStatus(
            location = GreatConditionsWithoutAlert.location,
            windSpeed = GreatConditionsWithoutAlert.WIND_SPEED,
            windDir = GreatConditionsWithoutAlert.windDir,
            waveHeight = GreatConditionsWithoutAlert.WAVE_HEIGHT,
            waveDir = GreatConditionsWithoutAlert.waveDir,
            wavePeriod = GreatConditionsWithoutAlert.WAVE_PERIOD,
        )
        assert(status == ConditionStatus.GREAT) {"Status should be Utmerket but was $status"}
    }
    @Test
    fun conditionsAreGreatWithOppositeWind() {
        val status = conditionsUtils.getConditionStatus(
            location = GreatConditionsWithoutAlertOppositeWind.location,
            windSpeed = GreatConditionsWithoutAlertOppositeWind.WIND_SPEED,
            windDir = GreatConditionsWithoutAlertOppositeWind.windDir,
            waveHeight = GreatConditionsWithoutAlertOppositeWind.WAVE_HEIGHT,
            waveDir = GreatConditionsWithoutAlertOppositeWind.waveDir,
            wavePeriod = GreatConditionsWithoutAlertOppositeWind.WAVE_PERIOD,
        )
        assert(status == ConditionStatus.GREAT) { "Status should be Utmerket but was $status" }
    }
}