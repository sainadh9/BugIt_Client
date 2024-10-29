package com.bugit.bugit.bugappscope.data

import com.bugit.bugit.bugappscope.domain.entities.CreateRowResponse
import com.bugit.bugit.bugappscope.domain.entities.CreateSheetResponse
import com.bugit.bugit.bugappscope.domain.entities.GetAllSheetsResponse
import com.bugit.bugit.bugappscope.domain.entities.OAuthResponse
import com.bugit.bugit.bugappscope.domain.entities.PostCreateSheetModel
import com.bugit.bugit.bugappscope.domain.entities.PostRowToSheet
import com.bugit.bugit.utils.Constants.CREATE_ROW
import com.bugit.bugit.utils.Constants.CREATE_SHEET
import com.bugit.bugit.utils.Constants.GET_ALL_SHEETDATA
import com.bugit.bugit.utils.Constants.REFRESH_OAUTH_TOKEN
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiServiceInterface {


    @GET(GET_ALL_SHEETDATA)
    suspend fun getAllSheetData(
        @Query("key") key: String?
    ): Response<GetAllSheetsResponse>

    @POST(CREATE_SHEET)
    suspend fun postCreateSheet(
        @Body createSheetModel: PostCreateSheetModel,
        @Query("key") key: String?
    ): Response<CreateSheetResponse>


    @POST(CREATE_ROW + "{range}:append")
    suspend fun postCreateRow(
        @Path("range") range: String,
        @Query("insertDataOption") insertDataOption: String?,
        @Query("valueInputOption") valueInputOption: String?,
        @Query("key") key: String?,
        @Body postRowToSheet: PostRowToSheet,
    ): Response<CreateRowResponse>

    @POST(REFRESH_OAUTH_TOKEN)
    @FormUrlEncoded
    fun postRefreshToken(
        @Field("grant_type") grant_type: String,
        @Field("refresh_token") refresh_token: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
    ): Response<OAuthResponse>



}