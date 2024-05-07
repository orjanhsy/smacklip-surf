package com.example.myapplication.model.surfareas

import com.example.myapplication.R


enum class SurfArea(
    val locationName: String,
    val areaName: String,
    val lat: Double,
    val lon: Double,
    val image: Int,
    val optimalWaveDir: Double,
    val optimalWindDir: Double,
    val description: Int,
    val modelName: String,
    val pointId: Int

    //nye = Sola, hellestø, Brusand, Sandvesand, Mjølhussand
) {
    HODDEVIK(
        locationName = "Hoddevik",
        areaName = "Stadt",
        lat = 62.12503333,
        lon = 5.1493,
        image = R.drawable.cover___hoddevik,
        optimalWaveDir = (300.0 + 180.0) % 360.0,
        optimalWindDir = 300.0,
        description = R.string.description_hoddevik,
        modelName = "stad20143x2v",
        pointId = 2
    ),

    ERVIKA(
        locationName = "Ervika",
        areaName = "Stadt",
        lat = 62.17145,
        lon = 5.0993833,
        image = R.drawable.cover__ervika ,
        optimalWaveDir = (310.0 + 180.0) % 360.0,
        optimalWindDir = 310.0,
        description = R.string.description_ervika,
        modelName = "stad20143x2v",
        pointId = 2
    ),

    //Lofoten
    SKAGSANDEN(
        locationName = "Skagsanden",
        areaName = "Lofoten",
        lat = 68.1111333,
        lon = 13.2825,
        image = R.drawable.cover__skagsanden,
        optimalWaveDir = (300.0 + 180.0) % 360.0,
        optimalWindDir = 300.0,
        description = R.string.description_skagsanden,
        modelName = "lofoten2v",
        pointId = 9
    ),

    UNSTAD(
        locationName = "Unstad",
        areaName = "Lofoten",
        lat = 68.2715333,
        lon = 13.5636167,
        image = R.drawable.cover__unstad ,
        optimalWaveDir = (320.0 + 180.0) % 360.0,
        optimalWindDir = .0, //TODO: fIx
        description = R.string.description_unstad,
        modelName = "lofoten2v",
        pointId = 10
    ),

    //Sør-vest
    JAEREN(
        locationName = "Boresanden",
        areaName = "Jæren",
        lat = 58.79925,
        lon = 5.5371167,
        image = R.drawable.cover__jeren,
        optimalWaveDir = (270.0 + 180.0) % 360.0,
        optimalWindDir = 270.0,
        description = R.string.description_jaeren,
        modelName = "rogaland2v",
        pointId = 36
    ),

    SOLA(
        locationName = "Solastranden",
        areaName = "Jæren",
        lat = 58.88275,
        lon = 5.5948667,
        image = R.drawable.cover_sola,
        optimalWaveDir = (270 + 180.0) % 360.0,
        optimalWindDir = 270.0,
        description = R.string.description_sola,
        modelName = "rogaland2v",
        pointId = 35
    ),

    HELLESTO(
        locationName = "Hellestø",
        areaName = "Jæren",
        lat = 58.842467,
        lon = 5.548417,
        image = R.drawable.cover_hellesto,
        optimalWaveDir = (285.0 + 180.0) % 360.0,
        optimalWindDir = 285.0,
        description = R.string.description_hellesto,
        modelName = "rogaland2v",
        pointId = 35
    ),

    BRUSAND(
        locationName = "Brusandstranden",
        areaName = "Jæren",
        lat = 58.53045,
        lon = 5.7426333,
        image = R.drawable.cover_brusand,
        optimalWaveDir = (215.0 + 180.0) % 360.0,
        optimalWindDir = 215.0,
        description = R.string.description_brusand,
        modelName = "jaren2v",
        pointId = 25
    ),

    STAVASANDEN(
        locationName = "Stavasanden",
        areaName = "Karmøy",
        lat = 59.236633,
        lon = 5.1712,
        image = R.drawable.cover_stavasanden,
        optimalWaveDir = (320.0 + 180.0) % 360.0,
        optimalWindDir = 320.0,
        description = R.string.description_stavasanden,
        modelName = "rogaland2v",
        pointId = 55
    ),

    SANDVESAND(
        locationName = "Sandvesand",
        areaName = "Karmøy",
        lat = 59.1682,
        lon = 5.1853,
        image = R.drawable.cover_sandvesanden,
        optimalWaveDir = (230.0 + 180.0) % 360.0,
        optimalWindDir = 230.0,
        description = R.string.description_sandvesand,
        modelName = "rogaland2v",
        pointId = 34
    ),

    MJOLHUSSAND(
        "Mjølhussand",
        "Karmøy",
        59.1683,
        5.18575,
        R.drawable.cover_mjolhussanden,
        (275.0 + 180.0) % 360.0,
        275.0,
        R.string.description_mjolhussand,
        modelName = "rogaland2v",
        pointId = 34
    ),

    //Østlandet
    SALTSTEIN(
        locationName = "Saltstein",
        areaName = "Mølen",
        lat = 58.964617,
        lon = 9.829667,
        image = R.drawable.cover_saltstein,
        optimalWaveDir = (190.0 + 180.0) % 360.0,
        optimalWindDir = 190.0,
        description = R.string.description_saltstein,
        modelName = "ytre_oslofjord2v",
        pointId = 1
    )
}

