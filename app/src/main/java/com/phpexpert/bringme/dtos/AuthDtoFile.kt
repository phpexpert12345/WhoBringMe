package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName


/*{
    "status_code": "0",
    "status_message": "Authentication Success",
    "data": [
    {
        "auth_key":
        "dH_8Rpyla-E:APA91bFNJsbcya80FU0SrsxtzEXR3qdK100EzdZykXtpQCkZgzp10tfaSC7PjDFSn7JJ6eFsAObjAN6Y_fr4kO8PnhW2PcMkjYVjDZODnf7Iou0AgYYWh0vFRolOVB5d1UTYRfC7gdSB",
        "currency_code": "EUR",
        "GOOGLE_MAP_KEY": "AIzaSyAfySREHfRw2x8bEFT6b7Nc4z3Te80LiyI"
    }
    ]
}*/
class AuthDtoMain {
    @SerializedName("status_code")
    var status_code: String? = ""

    @SerializedName("status_message")
    var status_message: String? = ""

    @SerializedName("data")
    var data: ArrayList<AuthDtoData>? = ArrayList()

}

class AuthDtoData {
    @SerializedName("auth_key")
    var auth_key: String? = ""

    @SerializedName("currency_code")
    var currency_code: String? = ""

    @SerializedName("GOOGLE_MAP_KEY")
    var GOOGLE_MAP_KEY: String? = ""
}