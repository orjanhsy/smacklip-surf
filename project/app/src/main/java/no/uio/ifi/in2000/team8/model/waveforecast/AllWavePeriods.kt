package no.uio.ifi.in2000.team8.model.waveforecast

import no.uio.ifi.in2000.team8.model.surfareas.SurfArea

data class AllWavePeriods(
    val wavePeriods: Map<SurfArea, Map<Int, List<Double?>>> = mapOf()
)
