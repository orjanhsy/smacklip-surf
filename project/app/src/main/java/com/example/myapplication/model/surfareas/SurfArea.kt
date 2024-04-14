package com.example.myapplication.model.surfareas

import com.example.myapplication.R


enum class SurfArea(
    val locationName: String,
    val lat: Double,
    val lon: Double,
    val image: Int,
    val direction: Int,
    val description: String,
    val modelName: String = "",
    val pointId: Int = 0,
) {
    //surf areas

    //Stadt
    HODDEVIK("Hoddevik",62.723, 5.103, R.drawable.cover___hoddevik, 300, "description", modelName = "jomfruland4x2v", pointId = 15),
    ERVIKA("Ervika", 62.166674, 5.115609, 0, 310, "description", modelName = "stad20143x2v", pointId = 3),

    //Lofoten
    SKAGSANDEN("Skagsanden", 68.107052, 13.295348, 0, 300, "description", modelName = "lofoten2v", pointId = 9),
    UNSTAD("Unstad", 68.268527, 13.580834, 0, 320, "description", modelName = "vestfjordenytre3x2v", pointId = 6),
    GIMSTAD("Gimstad", 68.637591, 14.427877, 0, 270, "description", modelName = "vestfjordenindre3x2v", pointId = 1),
    SANDVIKBUKTA("Sandvikbukta", 68.757964, 14.470910, 0, 230, "description", modelName = "vesteralen2v", pointId = 20),

    //Sør-vest
    JAEREN("Jæren (Boresanden)", 58.800230, 5.548844, 0, 270, "description", modelName = "rogaland2v", pointId = 55),
    KARMOEY("Karmøy (Stavasanden)", 59.233526, 5.183540, 0, 320, "description", modelName = "rogaland2v", pointId = 34),

    //Østlandet
    SALTSTEIN("Saltstein", 58.969619, 9.832590, 0, 190, "description", modelName = "ytre_oslofjord2v", pointId = 26),


    //for tests
    NORDKAPP("Nordkapp",71.1655, 25.7992, 0, 360, "description", modelName = "nordkapp2v", pointId = 7),
    FEDJE("Fedje",60.7789, 4.71486, 0, 360, "description", modelName = "hordaland2x2v", pointId = 1),


}