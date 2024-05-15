package no.uio.ifi.in2000.team8.model.surfareas

import no.uio.ifi.in2000.team8.R


enum class SurfArea(
    val locationName: String,
    val areaName: String,
    val lat: Double,
    val lon: Double,
    val image: Int,
    val optimalWaveDir: Double,
    val optimalWindDir: Double,
    val description: Int,

    //nye = Sola, hellestø, Brusand, Sandvesand, Mjølhussand
) {
    HODDEVIK(
        locationName = "Hoddevik",
        areaName = "Stad",
        lat = 62.12503333,
        lon = 5.1493,
        image = R.drawable.cover___hoddevik,
        optimalWaveDir = 300.0,
        optimalWindDir = (300.0 + 180.0) % 360.0,
        description = R.string.description_hoddevik,
    ),

    ERVIKA(
        locationName = "Ervika",
        areaName = "Stad",
        lat = 62.17145,
        lon = 5.0993833,
        image = R.drawable.cover__ervika ,
        optimalWaveDir = 310.0 ,
        optimalWindDir = (310.0 + 180.0) % 360.0,
        description = R.string.description_ervika,
    ),

    //Lofoten
    SKAGSANDEN(
        locationName = "Skagsanden",
        areaName = "Lofoten",
        lat = 68.1111333,
        lon = 13.2825,
        image = R.drawable.cover__skagsanden,
        optimalWaveDir = 300.0,
        optimalWindDir = (300.0 + 180.0) % 360.0,
        description = R.string.description_skagsanden,
    ),

    UNSTAD(
        locationName = "Unstad",
        areaName = "Lofoten",
        lat = 68.2715333,
        lon = 13.5636167,
        image = R.drawable.cover__unstad ,
        optimalWaveDir = 320.0,
        optimalWindDir = (320.0 + 180.0) % 360.0,
        description = R.string.description_unstad,
    ),

    //Sør-vest
    JAEREN(
        locationName = "Boresanden",
        areaName = "Jæren",
        lat = 58.79925,
        lon = 5.5371167,
        image = R.drawable.cover__jeren,
        optimalWaveDir = 270.0,
        optimalWindDir = (270.0 + 180.0) % 360.0,
        description = R.string.description_jaeren,
    ),

    SOLA(
        locationName = "Solastranden",
        areaName = "Jæren",
        lat = 58.88275,
        lon = 5.5948667,
        image = R.drawable.cover_sola,
        optimalWaveDir = 270.0,
        optimalWindDir = (270 + 180.0) % 360.0,
        description = R.string.description_sola,
    ),

    HELLESTO(
        locationName = "Hellestø",
        areaName = "Jæren",
        lat = 58.842467,
        lon = 5.548417,
        image = R.drawable.cover_hellesto,
        optimalWaveDir = 285.0,
        optimalWindDir = (285.0 + 180.0) % 360.0,
        description = R.string.description_hellesto,
    ),

    BRUSAND(
        locationName = "Brusandstranden",
        areaName = "Jæren",
        lat = 58.53045,
        lon = 5.7426333,
        image = R.drawable.cover_brusand,
        optimalWaveDir = 215.0,
        optimalWindDir = (215.0 + 180.0) % 360.0,
        description = R.string.description_brusand,
    ),

    STAVASANDEN(
        locationName = "Stavasanden",
        areaName = "Karmøy",
        lat = 59.236633,
        lon = 5.1712,
        image = R.drawable.cover_stavasanden,
        optimalWaveDir = 320.0,
        optimalWindDir = (320.0 + 180.0) % 360.0,
        description = R.string.description_stavasanden,
    ),

    SANDVESAND(
        locationName = "Sandvesand",
        areaName = "Karmøy",
        lat = 59.1682,
        lon = 5.1853,
        image = R.drawable.cover_sandvesanden,
        optimalWaveDir = 230.0,
        optimalWindDir = (230.0 + 180.0) % 360.0,
        description = R.string.description_sandvesand,
    ),

    MJOLHUSSAND(
        locationName = "Mjølhussand",
        areaName = "Karmøy",
        lat = 59.1683,
        lon = 5.18575,
        image = R.drawable.cover_mjolhussanden,
        optimalWaveDir = 275.0,
        optimalWindDir = (275.0 + 180.0) % 360.0,
        description = R.string.description_mjolhussand,
    ),

    //Østlandet
    SALTSTEIN(
        locationName = "Saltstein",
        areaName = "Mølen",
        lat = 58.964617,
        lon = 9.829667,
        image = R.drawable.cover_saltstein,
        optimalWaveDir = 190.0,
        optimalWindDir = (190.0 + 180.0) % 360.0,
        description = R.string.description_saltstein,
    )
}

