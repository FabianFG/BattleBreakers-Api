package me.fungames.battlebreakers.api.network.services

import me.fungames.battlebreakers.api.model.EpicExchangeCode
import retrofit2.Call
import retrofit2.http.*

interface EpicGamesService {
    companion object {
        const val BASE_URL = "https://www.epicgames.com"
    }

    @GET("/id/api/csrf")
    fun csrfToken(): Call<Void>

    @FormUrlEncoded
    @POST("/id/api/login")
    fun login(@FieldMap fields: Map<String, String>, @Header("X-XSRF-TOKEN") xsfrToken : String) : Call<Void>

    /**
     * @param ssoSession must start with EPIC_SSO_SESSION=
     */
    @GET("/id/api/exchange")
    fun exchange(@Header("Cookie") ssoSession : String) : Call<EpicExchangeCode>
}