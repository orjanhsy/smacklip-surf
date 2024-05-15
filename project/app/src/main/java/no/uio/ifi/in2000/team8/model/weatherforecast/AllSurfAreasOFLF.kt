package no.uio.ifi.in2000.team8.model.weatherforecast

import no.uio.ifi.in2000.team8.model.surfareas.SurfArea

data class AllSurfAreasOFLF (
    val next7Days: Map<SurfArea, Forecast7DaysOFLF> = mapOf()
)