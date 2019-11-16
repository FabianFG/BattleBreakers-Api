package me.fungames.battlebreakers.api

import me.fungames.battlebreakers.api.events.Event
import me.fungames.battlebreakers.api.exceptions.EpicErrorException
import me.fungames.battlebreakers.api.network.services.AccountPublicService
import me.fungames.battlebreakers.api.network.services.EpicGamesService
import me.fungames.battlebreakers.api.network.services.WexPublicService


interface BattleBreakersApi {
    val isLoggedIn: Boolean

    @Throws(EpicErrorException::class)
    fun loginClientCredentials()
    @Throws(EpicErrorException::class)
    fun login(email : String, password : String, rememberMe : Boolean = false)

    @Throws(EpicErrorException::class)
    fun logout()

    fun fireEvent(event: Event)

    //All the Services available
    val epicGamesService : EpicGamesService
    val accountPublicService: AccountPublicService
    val wexPublicService : WexPublicService


    val language: String
    val accountTokenType: String

    val accountToken: String
}