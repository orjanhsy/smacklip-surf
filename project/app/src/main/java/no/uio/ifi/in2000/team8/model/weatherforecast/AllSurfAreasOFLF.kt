package no.uio.ifi.in2000.team8.model.weatherforecast

import no.uio.ifi.in2000.team8.model.surfareas.SurfArea

data class AllSurfAreasOFLF (
    val forecasts: Map<SurfArea, Forecast7DaysOFLF> = mapOf()
)