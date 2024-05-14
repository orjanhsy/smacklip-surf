package no.uio.ifi.in2000.team8.model.oceanforecast

data class PropertiesOF(
    val meta: MetaOF,
    val timeseries: List<TimeserieOF>
)