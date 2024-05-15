package no.uio.ifi.in2000.team8.model.oceanforecast

data class OceanForecast(
    val geometry: GeometryOF,
    val properties: PropertiesOF,
    val type: String
)