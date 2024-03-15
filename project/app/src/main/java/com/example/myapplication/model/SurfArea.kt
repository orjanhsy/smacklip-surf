package com.example.myapplication.model

import com.example.myapplication.R


enum class SurfArea(
    val lat: Double,
    val lon: Double,
    val image: Int
) {
    HODDEVIK(62.723, 5.103, R.drawable.cover___hoddevik),
    NORDKAPP(71.1655, 25.7992, 0),
    FEDJE(60.7789, 4.71486, 0)
}