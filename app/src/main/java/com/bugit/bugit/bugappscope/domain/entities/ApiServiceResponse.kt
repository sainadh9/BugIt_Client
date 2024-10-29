package com.bugit.bugit.bugappscope.domain.entities

import com.google.gson.annotations.SerializedName

data class CreateSheetResponse(

    var spreadsheetId: String? = null,
    var replies: ArrayList<Replies>? = null

)

data class Replies(var addSheet: AddSheetResponse? = AddSheetResponse())


data class AddSheetResponse(

    var properties: PropertiesResponse? = PropertiesResponse()

)

data class PropertiesResponse(

    var sheetId: Int? = null,
    var title: String? = null,
    var index: Int? = null,
    var sheetType: String? = null,
    var gridProperties: GridProperties? = GridProperties()

)

data class GridProperties(

    var rowCount: Int? = null,
    var columnCount: Int? = null

)


data class CreateRowResponse(

    var spreadsheetId: String? = null,
    var tableRange: String? = null,
    var updates: Updates? = Updates()

)


data class Updates(

    var spreadsheetId: String? = null,
    var updatedRange: String? = null,
    var updatedRows: Int? = null,
    var updatedColumns: Int? = null,
    var updatedCells: Int? = null

)


data class GetAllSheetsResponse(

    var spreadsheetId: String? = null,
    var sheets: List<Sheets>? = null,
    var spreadsheetUrl: String? = null

)

data class Sheets(

    var properties: PropertiesResponse? = null

)

data class OAuthResponse(

    @SerializedName("access_token") var access_Token: String? = null,
    @SerializedName("expires_in") var expiresIn: Int? = null,
    var scope: String? = null,
    @SerializedName("token_type") var tokenType: String? = null

)