package com.bugit.bugit.bugappscope.data

import com.bugit.bugit.bugappscope.domain.entities.OAuthResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenServiceInterface {

    @POST("token")
    @FormUrlEncoded
   suspend fun postRefreshToken(
        @Field("grant_type") grant_type: String,
        @Field("refresh_token") refresh_token: String,
        @Field("client_id") client_id: String,
        @Field("client_secret") client_secret: String,
    ): Response<OAuthResponse>


}