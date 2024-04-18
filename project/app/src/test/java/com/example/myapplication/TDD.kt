package com.example.myapplication

import androidx.compose.foundation.gestures.ModifierLocalScrollableContainerProvider.value
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.data.smackLip.SmackLipRepository
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.home.HomeScreen
import com.example.myapplication.ui.home.HomeScreenViewModel
import com.example.myapplication.ui.surfarea.SurfAreaScreenViewModel
import org.junit.Test

class TDD {

    //  repository: smacklip
    private val repo: SmackLipRepository = SmackLipRepositoryImpl()

    // VM: surfAreaScreenVM
    private val vm = SurfAreaScreenViewModel()

    // test objects
    @Test
    fun conditionsArePOORIfAlertsArePresent() {
        vm.updateForecastNext7Days(SurfArea.HODDEVIK)
        vm.updateAlerts()
        val hoddevik = vm.surfAreaScreenUiState.value

        val status = repo.getConditionStatus(
            hoddevik.windSpeeds[0][0].second,
            hoddevik.windSpeedOfGusts[0][0].second,
            hoddevik.windDirections[0][0].second,
            hoddevik.waveHeights[0][0].second,
            hoddevik.waveDirs[0][0].second,
            hoddevik.wavePeriods[0][0].second,
            hoddevik.alerts
        )

    }
}