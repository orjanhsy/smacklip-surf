package com.example.myapplication.model.surfareas

import com.example.myapplication.R


enum class SurfArea(
    val locationName: String,
    val lat: Double,
    val lon: Double,
    val image: Int,
    val direction: Int,
    val description: String,
    val modelName: String,
    val pointId: Int

) {
    HODDEVIK("Hoddevik",62.1237, 5.1615833, R.drawable.cover___hoddevik, 300, "Det kommer surfere fra hele verden for å oppleve bølgene i Hoddevik. Mye på grunn av det praktfulle landskapet med hvite sandstrender omkranset av høye fjellsider, men også fordi det ekstra godt egnet å surfe under ekstreme vindforhold fordi de høye fjellene skjermer stranden for den verste vinden. ", modelName = "stad20143x2v", pointId = 2),
    ERVIKA("Ervika", 62.166674, 5.115609,R.drawable.cover__ervika , 310, "Ytterst på Stadlandet inne i en vik, finner du den historiske bygda Ervik. Her finner du Ervikstranda som også er en av de beste surfestrendene i Norge. Det er alltid mye bølger her som gjør det perfekt for avanserte surfere, men vær oppmerksom og ta forhåndsregler for det kan være en del undervannsstrømmer og vrakrester her. Her blir surfinga en fantastisk naturopplevelse med krystallklart vann og majestetiske fjell på alle kanter. På Ervikstranda har du nesten bølgegaranti, med bølger som slår inn både fra sørvest og nordvest.  ", modelName = "stad20143x2v", pointId = 2),

    //Lofoten
    SKAGSANDEN("Skagsanden", 68.107052, 13.295348, R.drawable.cover__skagsanden, 300, "description", modelName = "lofoten2v", pointId = 9),
    UNSTAD("Unstad", 68.268527, 13.580834,R.drawable.cover__unstad , 320, "description", modelName = "lofoten2v", pointId = 10),
    GIMSTAD("Gimstad", 68.637591, 14.427877, 0, 270, "description", modelName = "vesteralen2v", pointId = 20),
    SANDVIKBUKTA("Sandvikbukta", 68.757964, 14.470910, 0, 230, "description", modelName = "vesteralen2v", pointId = 20),

    //Sør-vest
    JAEREN("Jæren (Boresanden)", 58.800230, 5.548844, R.drawable.cover__jeren, 270, "description", modelName = "rogaland2v", pointId = 36),
    KARMOEY("Karmøy (Stavasanden)", 59.233526, 5.183540, 0, 320, "description", modelName = "rogaland2v", pointId = 55),

    //Østlandet
    SALTSTEIN("Saltstein", 58.969619, 9.832590, 0, 190, "description", modelName = "ytre_oslofjord2v", pointId = 1),


    //for tests
    NORDKAPP("Nordkapp",71.1655, 25.7992, 0, 360, "description", modelName = "nordkapp2v", pointId = 5),
    FEDJE("Fedje",60.7789, 4.71486, 0, 360, "description", modelName = "bremanger2v", pointId = 127),

}

