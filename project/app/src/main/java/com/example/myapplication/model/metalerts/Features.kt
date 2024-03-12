package com.example.myapplication.model.metalerts

import com.google.gson.annotations.SerializedName

data class Features (

    @SerializedName("geometry"   ) var geometry   : Geometry?   = Geometry(),
    @SerializedName("properties" ) var properties : Properties? = Properties(),
    @SerializedName("type"       ) var type       : String?     = null,
    @SerializedName("when"       ) var timeInterval: TimeInterval? = TimeInterval()

)
