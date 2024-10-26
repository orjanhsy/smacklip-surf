package no.uio.ifi.in2000.team8.model.metalerts

import com.google.gson.annotations.SerializedName

data class Properties (
    @SerializedName("MunicipalityId"       ) var municipalityId       : String?              = null,
    @SerializedName("administrativeId"     ) var administrativeId     : String?              = null,
    @SerializedName("area"                 ) var area                 : String?              = null,
    @SerializedName("awarenessResponse"    ) var awarenessResponse    : String?              = null,
    @SerializedName("awarenessSeriousness" ) var awarenessSeriousness : String?              = null,
    @SerializedName("awareness_level"      ) var awarenessLevel       : String?              = null,
    @SerializedName("awareness_type"       ) var awarenessType        : String?              = null,
    @SerializedName("certainty"            ) var certainty            : String?              = null,
    @SerializedName("consequences"         ) var consequences         : String?              = null,
    @SerializedName("county"               ) var county               : ArrayList<String>    = arrayListOf(),
    @SerializedName("description"          ) var description          : String?              = null,
    @SerializedName("event"                ) var event                : String?              = null,
    @SerializedName("eventAwarenessName"   ) var eventAwarenessName   : String?              = null,
    @SerializedName("eventEndingTime"      ) var eventEndingTime      : String?              = null,
    @SerializedName("geographicDomain"     ) var geographicDomain     : String?              = null,
    @SerializedName("id"                   ) var id                   : String?              = null,
    @SerializedName("instruction"          ) var instruction          : String?              = null,
    @SerializedName("resources"            ) var resources            : ArrayList<Resources> = arrayListOf(),
    @SerializedName("riskMatrixColor"      ) var riskMatrixColor      : String?              = null,
    @SerializedName("severity"             ) var severity             : String?              = null,
    @SerializedName("title"                ) var title                : String?              = null,
    @SerializedName("triggerLevel"         ) var triggerLevel         : String?              = null,
    @SerializedName("type"                 ) var type                 : String?              = null

)