package com.bugit.bugit.bugappscope.network

import android.content.Context
import com.bugit.bugit.bugappscope.data.TokenServiceInterface
import com.bugit.bugit.local.BugPref
import com.bugit.bugit.utils.Constants.CLIENT_ID
import com.bugit.bugit.utils.Constants.CLIENT_SECRET
import com.bugit.bugit.utils.Constants.GRAND_TYPE
import com.bugit.bugit.utils.Constants.REFERENCE_OAUTH_TOKEN
import com.bugit.bugit.utils.Constants.REFRESH_TOKEN
import com.bugit.bugit.utils.UniversalManager.showToastMethod
import com.bugit.bugit.utils.UniversalManager.throwError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenServiceInterface: TokenServiceInterface,
    private val bugPref: BugPref,
    val context: Context
) : Interceptor {
    var newToken: String? = ""


    override fun intercept(chain: Interceptor.Chain): Response {
        var oldToken: String? = ""
        val originalRequest = chain.request()
        runBlocking {
            oldToken = bugPref.getLatestToken().first()
            if (oldToken.isNullOrEmpty())
                oldToken = REFERENCE_OAUTH_TOKEN

        }

        val request = originalRequest.newBuilder()
            .addHeader("accept", "application/json")
            .addHeader("Authorization", "Bearer ${oldToken}")
            .build()
        val response = chain.proceed(request)
        try {
            if (response.code == HttpURLConnection.HTTP_UNAUTHORIZED) {
                synchronized(this) {
                    newToken = refreshToken()
                    if (newToken!!.isNotEmpty()) {
                        MainScope().launch(Dispatchers.IO) {
                            bugPref.saveOauthToken(newToken!!)
                            oldToken = newToken
                        }
                    }
                    val newRequest = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $newToken")
                        .build()
                    response.close()
                    return chain.proceed(newRequest)

                }

            }
            return response
        } catch (e: Exception) {
            throwError(e, context)

            // Handle exceptions, e.g., network errors, token refresh failures
            throw e
        }
    }


    fun refreshToken(): String {
        var generatedToken = ""
        // Make a network request to refresh the token using the refresh token
        // Update the access token and refresh token in SharedPreferences
        // Return the new access token
        try {
            runBlocking {
                val deferred = async {
                    tokenServiceInterface.postRefreshToken(
                        GRAND_TYPE,
                        REFRESH_TOKEN,
                        CLIENT_ID,
                        CLIENT_SECRET
                    )
                }

                val response = deferred.await()
                if (response.isSuccessful) {
                    generatedToken = response.body()?.access_Token!!
                    return@runBlocking
                } else {
                    showToastMethod(context, response.errorBody()?.string(), true)

                }
            }


        } catch (e: Exception) {

            throwError(e, context)

        }
        return generatedToken
    }


}


