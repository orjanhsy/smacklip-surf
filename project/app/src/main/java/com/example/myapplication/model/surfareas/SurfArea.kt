package com.example.myapplication.model.surfareas

import com.example.myapplication.R


enum class SurfArea(
    val locationName: String,
    val lat: Double,
    val lon: Double,
    val image: Int,
    val direction: Int,
    val description: String,
) {
    //surf areas

    //Stadt
    HODDEVIK("Hoddevik",62.723, 5.103, R.drawable.cover___hoddevik, 300, "description"),
    ERVIKA("Ervika", 62.166674, 5.115609, 0, 310, "description"),

    //Lofoten
    SKAGSANDEN("Skagsanden", 68.107052, 13.295348, 0, 300, "description"),
    UNSTAD("Unstad", 68.268527, 13.580834, 0, 320, "description"),
    GIMSTAD("Gimstad", 68.637591, 14.427877, 0, 270, "description"),
    SANDVIKBUKTA("Sandvikbukta", 68.757964, 14.470910, 0, 230, "description"),

    //Sør-vest
    JAEREN("Jæren (Boresanden)", 58.800230, 5.548844, 0, 270, "description"),
    KARMOEY("Karmøy (Stavasanden)", 59.233526, 5.183540, 0, 320, "description"),

    //Østlandet
    SALTSTEIN("Saltstein", 58.969619, 9.832590, 0, 190, "description"),


    //for tests
    NORDKAPP("Nordkapp",71.1655, 25.7992, 0, 360, "description"),
    FEDJE("Fedje",60.7789, 4.71486, 0, 360, "description"),


}