package no.uio.ifi.in2000.team8.model.weatherforecast

data class DataAtTime (
    val windSpeed: Double,
    val windGust: Double,
    val windDir: Double,
    val airTemp: Double,
    val symbolCode: String,
    val waveHeight: Double,
    val waveDir: Double
)