package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName
import java.util.*

class PlaceMainClass {
    @SerializedName("results")
    var results: ArrayList<PlaceAutocomplete> = ArrayList<PlaceAutocomplete>()
}

class PlaceAutocomplete {
    @SerializedName("formatted_address")
    var formatted_address: String? = null

    @SerializedName("geometry")
    var geometry: PlaceGeometryClass? = null
}
class PlaceGeometryClass {
    @SerializedName("location")
    var location: PlaceLocationClass? = null

}

class PlaceLocationClass {
    @SerializedName("lat")
    var lat: String? = null

    @SerializedName("lng")
    var lng: String? = null
}
