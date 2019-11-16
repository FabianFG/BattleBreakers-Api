package me.fungames.battlebreakers.api.model

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import java.util.*
import com.google.gson.annotations.SerializedName
import me.fungames.battlebreakers.api.Utils
import retrofit2.Response
import java.io.IOException





data class DeviceAuth(
    var deviceId: String,
    var accountId: String,
    /**
     * only in device registration request
     */
    var secret: String,
    var userAgent: String,
    var deviceInfo: DeviceInfo,
    var created: LocationIpDate,
    var lastAccess: LocationIpDate
) {

    data class DeviceInfo(var type: String, var model: String, var os: String)

    data class LocationIpDate(
        var location: String,
        var ipAddress: String,
        var dateTime: Date
    )
}

data class ExchangeResponse(
    var code : String,
    var creatingClientId: String,
    var expiresInSeconds: Int
)

data class ExternalAuth(
    var accountId: String,
    var type: String,
    var authIds: List<AuthId>,
    var externalAuthId: String,
    var externalAuthSecondaryId: String,
    var dateAdded: Date,
    var externalDisplayName: String,
    var externalAuthIdType: String,
    var lastLogin: Date
) {
    data class AuthId(var id: String, var type: String)
}

open class GameProfile(
    id : String, displayName: String, @field:SerializedName("externalAuths") val externalAuths: Map<String, ExternalAuth>
) {
    @field:SerializedName("id") var id: String = id
        set(value) {
            require(value.length == 32) { "length != 32" }
            field = value
        }
    @field:SerializedName("displayName") var displayName: String = displayName
        set(value) {
            require(value.length >= 3) { "length < 3" }
            field = value
        }
}
open class BaseOauthResponse(
    var account_id: String,
    var client_id: String,
    var client_service: String,
    /**
     * Nonexistent if X-Epic-Device-Id header isn't provided
     */
    var device_id: String,
    var expires_at: Date,
    var expires_in: Int?,
    var in_app_id: String,
    var internal_client: Boolean?,
    var lastPasswordValidation: Date,
    var perms: Array<Perm>,
    var token_type: String
)

data class Perm(
    var resource: String, // flags
    var action: Int
)

class LoginResponse(
    account_id: String,
    client_id: String,
    client_service: String,
    device_id: String,
    expires_at: Date,
    expires_in: Int?,
    in_app_id: String,
    internal_client: Boolean?,
    lastPasswordValidation: Date,
    perms: Array<Perm>,
    token_type: String,
    var access_token: String,
    var app: String,
    var refresh_expires: String,
    var refresh_expires_at: Date,
    var refresh_token: String
) : BaseOauthResponse(
    account_id,
    client_id,
    client_service,
    device_id,
    expires_at,
    expires_in,
    in_app_id,
    internal_client,
    lastPasswordValidation,
    perms,
    token_type
)

data class QueryExternalIdMappingsByIdPayload(var ids: List<String>, var authType: String)

class VerifyResponse(
    account_id: String, client_id: String, client_service: String, device_id: String, expires_at: Date,
    expires_in: Int?, in_app_id: String, internal_client: Boolean?, lastPasswordValidation: Date, perms: Array<Perm>,
    token_type: String, var app: String, var auth_method: String, var session_id: String, var token: String
) : BaseOauthResponse(
    account_id,
    client_id,
    client_service,
    device_id,
    expires_at,
    expires_in,
    in_app_id,
    internal_client,
    lastPasswordValidation,
    perms,
    token_type
)

class XGameProfile(
    id: String,
    displayName: String,
    externalAuths: Map<String, ExternalAuth>,
    var name: String,
    var email: String,
    var failedLoginAttempts: Int?,
    var lastFailedLogin: String,
    var lastLogin: String,
    var numberOfDisplayNameChanges: Int?,
    var ageGroup: String,
    var headless: Boolean?,
    var country: String,
    var lastName: String,
    var phoneNumber: String,
    var preferredLanguage: String,
    var lastDisplayNameChange: Date,
    var canUpdateDisplayName: Boolean?,
    var tfaEnabled: Boolean?
) : GameProfile(id, displayName, externalAuths)

open class EpicError(
    var errorCode: String, var errorMessage: String, var messageVars: Array<String>, var numericErrorCode: Int?,
    var originatingService: String, var intent: String
) {

    val displayText: String
        get() = if (errorMessage.isEmpty()) errorCode else errorMessage

    companion object {

        fun parse(response: Response<*>): EpicError {
            return parse(
                response,
                EpicError::class.java
            )
        }

        fun <T : EpicError> parse(response: Response<*>, toErrorClass: Class<T>): T {
            try {
                return parse(
                    response.errorBody()!!.string(),
                    toErrorClass
                )
            } catch (e: IOException) {
                throw RuntimeException("Unexpected error whilst parsing error data", e)
            }

        }

        fun <T : EpicError> parse(s: String, toErrorClass: Class<T>): T {
            return Utils.DEFAULT_GSON.fromJson(s, toErrorClass)
        }
    }
}

data class EpicExchangeCode(var code : String)


data class CloudStorageResponse (
    val uniqueFilename : String,
    val filename : String,
    val hash : String,
    val hash256 : String,
    val length : Int,
    val contentType : String,
    val uploaded : String,
    val storageType : String,
    val doNotCache : Boolean
)

data class CalendarTimelineResponse (
        val channels : Channels,
        val eventsTimeOffsetHrs : Int,
        val cacheIntervalMins : Int,
        val currentTime : String
) {
    data class ActiveEvents (
        val sortPriority : Int,
        val eventAsset : String,
        val eventId : String,
        val expiresAt : String
    )

    data class ActiveNews (
        val uniqueId : String,
        val widget : String,
        val newsType : Int,
        val widgetParams : WidgetParams
    )

    data class ActiveZones (
        val sortPriority : Int,
        val zoneId : String,
        val maxRuns : Int,
        val resetCostMtx : Int,
        val flags : List<String>,
        val eventId : String,
        val dynamicTier : Int,
        val dynamicWorldLevel : Int,
        val expiresAt : String,
        val eventKey : String
    )

    data class Battlepass (
        val states : List<State>,
        val cacheExpire : String
    )

    data class BodyText (
        val en : String
    )

    data class BossZone (
        val zoneId : String,
        val availabilityBegin : String,
        val availabilityEnd : String,
        val minAccountLevel : Int,
        val maxAccountLevel : Int,
        val runLimit : Int
    )

    data class Channels (
        val news : News,
        val marketing : Marketing,
        @field:SerializedName("rotational-content") val rotationalContent : RotationalContent,
        @field:SerializedName("featured-stores-mcp") val featuredStoresMcp : FeaturedStoresMcp,
        @field:SerializedName("weekly-challenge") val weeklyChallenge : WeeklyChallenge,
        val battlepass : Battlepass
    )

    data class FeaturedStoresMcp (
        val states : List<State>,
        val cacheExpire : String
    )

    data class FoundInText (
        val en : String
    )

    data class Marketing (
        val states : List<State>,
        val cacheExpire : String
    )

    data class News (
        val states : List<State>,
        val cacheExpire : String
    )

    data class Phases (
        val zoneId : String,
        val availabilityBegin : String,
        val availabilityEnd : String,
        val minAccountLevel : Int,
        val maxAccountLevel : Int,
        val requirements : Requirements
    )

    data class Requirements (
        val maxPartySize : Int,
        val minPartySize : Int,
        val requirements : List<Requirements>,
        val excludeClasses : List<String>,
        val excludeElements : List<String>
    )

    data class RotationalContent (
        val states : List<State>,
        val cacheExpire : String
    )

    data class State (
        val validFrom : String,
        val activeEvents : List<String>,
        val state : JsonObject
    )

    class Storefront (
        //TODO
    )

    data class TitleText (
        val en : String
    )

    data class WeeklyChallenge (
        val states : List<State>,
        val cacheExpire : String
    )

    data class WidgetParams (
        val titleText : TitleText,
        val bodyText : BodyText,
        val foundInText : FoundInText,
        val linkMenu : String,
        val linkSubMenuClass : String,
        val backgroundImageUrl : String
    )
}

data class StorefrontsResponse (
    val refreshIntervalHrs : Int,
    val dailyPurchaseHrs : Int,
    val expiration : String,
    val storefronts : List<Storefronts>
) {

    data class Prices (
        val currencyType : String,
        val currencySubType : String,
        val regularPrice : Int,
        val finalPrice : Int,
        val saleType : String,
        val saleExpiration : String,
        val basePrice : Int
    )

    data class ItemGrants (
        val templateId : String,
        val quantity : Int
    )

    data class CatalogEntries (
        val offerId : String,
        val devName : String,
        val offerType : String,
        val prices : List<Prices>,
        val categories : List<String>,
        val dailyLimit : Int,
        val weeklyLimit : Int,
        val monthlyLimit : Int,
        val appStoreId : List<String>,
        val requirements : JsonElement,
        val metaInfo : JsonElement,
        val catalogGroup : String,
        val catalogGroupPriority : Int,
        val sortPriority : Int,
        val title : String,
        val shortDescription : String,
        val description : String,
        val displayAssetPath : String,
        val itemGrants : List<ItemGrants>
    )

    data class Storefronts (
        val name : String,
        val catalogEntries : List<CatalogEntries>
    )
}

data class ItemRatingsResponse (
    val myRating : MyRating,
    val overallRatings : OverallRatings
) {
    data class MyRating (
        val gameplayRating : Int,
        val appearanceRating : Int
    )

    data class Ratings (
        val gameplayRating : Int,
        val appearanceRating : Int
    )

    data class OverallRatings (
        val ratingsKey : String,
        val discussUrl : String,
        val ratings : List<Ratings>
    )
}

data class WexMcpResponse (
    val profileRevision : Int,
    val profileId : String,
    val profileChangesBaseRevision : Int,
    val profileChanges : List<JsonObject>,
    val notifications : List<Notifications>,
    val profileCommandRevision : Int,
    val serverTime : String,
    val responseVersion : Int
) {
    data class Notifications (
        val type : String,
        val primary : Boolean,
        val results : List<String>
    )
}



