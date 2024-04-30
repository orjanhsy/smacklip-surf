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
        lat = 62.1237,
        lon = 5.1615833,
        image = R.drawable.cover___hoddevik,
        optimalWaveDir = (300.0 + 180.0) % 360.0,
        optimalWindDir = 300.0,
        description = R.string.description_hoddevik,
        modelName = "stad20143x2v",
        pointId = 2
    ),

    ERVIKA(
        "Ervika",
        "Stadt",
        62.166674,
        5.115609,
        R.drawable.cover__ervika ,
        (310.0 + 180.0) % 360.0,
        310.0,
        description = R.string.description_ervika,
        modelName = "stad20143x2v",
        pointId = 2
    ),

    //Lofoten
    SKAGSANDEN(
        "Skagsanden",
        "Lofoten",
        68.107052,
        13.295348,
        R.drawable.cover__skagsanden,
        (300.0 + 180.0) % 360.0,
        300.0,
        R.string.description_skagsanden,
        modelName = "lofoten2v",
        pointId = 9
    ),

    UNSTAD(
        "Unstad",
        "Lofoten",
        68.268527,
        13.580834,
        R.drawable.cover__unstad ,
        (320.0 + 180.0) % 360.0,
        320.0,
        R.string.description_unstad,
        modelName = "lofoten2v",
        pointId = 10
    ),

    //Sør-vest
    JAEREN(
        "Boresanden",
        "Jæren",
        58.800230,
        5.548844,
        R.drawable.cover__jeren,
        (270.0 + 180.0) % 360.0,
        270.0,
        R.string.description_jaeren,
        modelName = "rogaland2v",
        pointId = 36
    ),
    SOLA(
        "Solastranden",
        "Jæren",
        58.884963,
        5.596460,
        R.drawable.cover_sola,
        (270 + 180.0) % 360.0,
        270.0,
        R.string.description_sola,
        modelName = "rogaland2v",
        pointId = 35
    ),

    HELLESTO(
        "Hellestø",
        "Jæren",
        58.841397,
        5.556923,
        R.drawable.cover_hellesto,
        (285.0 + 180.0) % 360.0,
        285.0,
        R.string.description_hellesto,
        modelName = "rogaland2v",
        pointId = 35
    ),

    BRUSAND(
        "Brusandstranden",
        "Jæren",
        58.533791,
        5.743437,
        R.drawable.cover_brusand,
        (215.0 + 180.0) % 360.0,
        215.0,
        R.string.description_brusand,
        modelName = "jaren2v",
        pointId = 25
    ),


    STAVASANDEN(
        "Stavasanden",
        "Karmøy",
        59.233526,
        5.183540,
        R.drawable.cover_stavasanden,
        (320.0 + 180.0) % 360.0,
        320.0,
        R.string.description_stavasanden,
        modelName = "rogaland2v",
        pointId = 55
    ),

    SANDVESAND(
        "Sandvesand",
        "Karmøy",
        59.170507,
        5.194763,
        R.drawable.cover_sandvesanden,
        (230.0 + 180.0) % 360.0,
        230.0,
        R.string.description_sandvesand,
        modelName = "rogaland2v",
        pointId = 34
    ),

    MJØLHUSSAND(
        "Mjølhussand",
        "Karmøy",
        59.168651,
        5.196098,
        R.drawable.cover_mjolhussanden,
        (275.0 + 180.0) % 360.0,
        275.0,
        R.string.description_mjolhussand,
        modelName = "rogaland2v",
        pointId = 34
    ),


    //Østlandet
    SALTSTEIN(
        "Saltstein",
        "Mølen",
        58.969619,
        9.832590,
        R.drawable.cover_saltstein,
        (190.0 + 180.0) % 360.0,
        190.0,
        R.string.description_saltstein,
        modelName = "ytre_oslofjord2v",
        pointId = 1
    ),
}

