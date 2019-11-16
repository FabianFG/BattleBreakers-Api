package me.fungames.battlebreakers.api.network.services

import com.google.gson.JsonElement
import me.fungames.battlebreakers.api.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface WexPublicService {
    companion object {
        const val BASE_URL = "https://wex-public-service-live-prod.ol.epicgames.com"
    }

    @GET("/wex/api/cloudstorage/system")
    fun cloudstorageList() : Call<List<CloudStorageResponse>>
    @GET("/wex/api/cloudstorage/system/{uniqueFilename}")
    fun downloadCloudstorageFile(@Path("uniqueFilename") uniqueFilename: String) : Call<ResponseBody>

    @GET("/wex/api/calendar/v1/timeline")
    fun calendarTimeline() : Call<CalendarTimelineResponse>

    @GET("/wex/api/storefront/v2/catalog")
    fun storefrontCatalog(@Query("rvn") rev : Int? = null) : Call<StorefrontsResponse>

    @GET("/wex/api/game/v2/item_ratings/{accountId}/{itemName}")
    fun itemRatings(@Path("accountId") accountId : String, @Path("itemName") itemName : String, @Query("rvn") rev : Int? = null) : Call<ItemRatingsResponse>

    /**
     * Seen commands:
     *  Reconcile (profile id = friends) (body = ReconcileCommand)
     *  UpdateFriends (profile id = friends) (body = UpdateFriendsCommand)
     *  GenerateDailyQuests (profile id = profile0) (body = GenerateDailyQuestsCommand)
     *  ClaimLoginReward (profile id = profile0) (body = ClaimLoginRewardCommand)
     *  ClaimGiftPoints (profile id = friends) (body = ClaimGiftPointsCommand)
     *  QueryProfile (profile id = profile0, friends, monsterpit, levels, multiplayer) (body = QueryProfileBody)
     *  MarkItemSeen (profile id = profile0) (body = MarkItemSeenCommand)
     *  SetRepHero (profile id = profile0) (body = SetRepHeroCommand)
     */
    @POST("/wex/api/game/v2/profile/{id}/{command}")
    fun clientCommand(
        @Path("command") command: String, @Path("id") accountId: String, @Query("profileId") profileId: String, @Query(
            "rvn"
        ) currentProfileRevision: Int?, @Header("X-EpicGames-ProfileRevisions") profileRevisionsMeta: String, @Body payload: Any
    ): Call<WexMcpResponse>
}