package com.example.myapplication.data.smackLip

import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.smacklip.AllSurfAreasOFLF
import com.example.myapplication.model.smacklip.Forecast7DaysOFLF
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.model.waveforecast.AllWavePeriods

interface Repository {
    val ofLfNext7Days: AllSurfAreasOFLF
    val wavePeriods: AllWavePeriods
    val alerts: Map<SurfArea, List<Alert>>
}