package me.fungames.battlebreakers.api

import me.fungames.battlebreakers.api.exceptions.EpicErrorException
import me.fungames.battlebreakers.api.model.LoginResponse
import java.io.IOException
import me.fungames.battlebreakers.api.model.EpicError
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import com.google.gson.GsonBuilder
import me.fungames.battlebreakers.api.events.Event
import me.fungames.battlebreakers.api.network.DefaultInterceptor
import me.fungames.battlebreakers.api.network.services.AccountPublicService
import me.fungames.battlebreakers.api.network.services.EpicGamesService
import me.fungames.battlebreakers.api.network.services.WexPublicService
import okhttp3.Cache
import retrofit2.Response
import java.util.*


class BattleBreakersApiImpl: BattleBreakersApi {


    var clientLauncherToken: String = Utils.CLIENT_LAUNCHER_TOKEN

    override var isLoggedIn = false
        private set

    override var language: String = "en"
        private set

    private val gson = GsonBuilder().create()!!
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .cache(Cache(Utils.cacheDirFile, 4 * 1024 * 1024))
        .addInterceptor(DefaultInterceptor(this))
        .build()
    private val retrofitBuilder: Retrofit.Builder

    override val epicGamesService : EpicGamesService
    override val accountPublicService: AccountPublicService
    override val wexPublicService: WexPublicService


    override val accountTokenType: String
        get() {
            checkNotNull(epicAccountTokenType) { "Api is not logged in" }
            return epicAccountTokenType!!
        }

    override val accountToken: String
        get() {
            checkNotNull(epicAccountAccessToken) { "Api is not logged in" }
            return epicAccountAccessToken!!
        }

    private var epicAccountTokenType : String? = null
    private var accountExpiresAt: Date? = null
    private var accountRefreshToken: String? = null
    private var epicAccountAccessToken: String? = null
    private var accountId: String? = null

    init {
        retrofitBuilder = Retrofit.Builder().client(httpClient).addConverterFactory(GsonConverterFactory.create(gson))
        accountPublicService =
        retrofitBuilder.baseUrl(AccountPublicService.BASE_URL).build().create(
            AccountPublicService::class.java)
        epicGamesService =
            retrofitBuilder.baseUrl(EpicGamesService.BASE_URL).build().create(
                EpicGamesService::class.java)
        wexPublicService =
            retrofitBuilder.baseUrl(WexPublicService.BASE_URL).build().create(
                WexPublicService::class.java)
    }

    @Throws(EpicErrorException::class)
    override fun loginClientCredentials() {
        val loginRequest =
            this.accountPublicService.grantToken("basic $clientLauncherToken", "client_credentials", emptyMap(), false)
        try {
            val response = loginRequest.execute()
            if (response.isSuccessful)
                loginSucceeded(response)
            else
                throw EpicErrorException(EpicError.parse(response))

        } catch (e: IOException) {
            throw EpicErrorException(e)
        }
    }

    override fun login(email: String, password: String, rememberMe: Boolean) {
        val csrf = this.epicGamesService.csrfToken().execute()
        if (!csrf.isSuccessful)
            throw EpicErrorException(EpicError.parse(csrf))
        val xsrfToken = csrf.headers().toMultimap()["Set-Cookie"]?.first { it.startsWith("XSRF-TOKEN=") }?.substringAfter("XSRF-TOKEN=")?.substringBefore(';')
            ?: throw EpicErrorException("Failed to obtain xsrf token")
        val login = this.epicGamesService.login(mapOf("email" to email, "password" to password, "rememberMe" to rememberMe.toString()), xsrfToken).execute()
        if (!login.isSuccessful)
            throw EpicErrorException(EpicError.parse(login))
        val ssoSession = login.raw().headers("Set-Cookie").first { it.startsWith("EPIC_SSO_SESSION=") }.substringBefore(';')
        val exchange = this.epicGamesService.exchange(ssoSession).execute()
        if (!exchange.isSuccessful)
            throw EpicErrorException(EpicError.parse(exchange))
        val exchangeCode = exchange.body()!!.code
        val auth = this.accountPublicService.grantToken("basic ${Utils.CLIENT_LAUNCHER_TOKEN}", "exchange_code", mapOf("exchange_code" to exchangeCode, "token_type" to "eg1"), null).execute()
        if (!auth.isSuccessful)
            throw EpicErrorException(EpicError.parse(auth))
        loginSucceeded(auth)
    }

    private fun loginSucceeded(response: Response<LoginResponse>) {
        val data = response.body()!!
        this.epicAccountAccessToken = data.access_token
        this.accountExpiresAt = data.expires_at
        this.accountId = data.account_id
        this.accountRefreshToken = data.refresh_token
        this.epicAccountTokenType = data.token_type
        this.isLoggedIn = true
    }

    @Throws(EpicErrorException::class)
    override fun logout() {
    }

    override fun fireEvent(event: Event) {
        println("Received " + event::class.java.simpleName)
    }
}