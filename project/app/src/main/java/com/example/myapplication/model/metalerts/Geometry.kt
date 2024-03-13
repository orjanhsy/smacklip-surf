package com.example.myapplication.model.metalerts

import com.google.gson.annotations.SerializedName

data class Geometry (

    @SerializedName("coordinates" ) var coordinates : List<List<List<Any>>>?  = null,
    @SerializedName("type"        ) var type        : String?        = null

)