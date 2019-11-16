package me.fungames.battlebreakers.api.model.commands

data class ReconcileCommand (
    val friendIdList : List<String>,
    val outgoingIdList : List<String>,
    val incomingIdList : List<String>
)

data class UpdateFriendsCommand(
    val friendInstanceId : String
)

class GenerateDailyQuestsCommand

class ClaimLoginRewardCommand

class ClaimGiftPointsCommand

class QueryProfileCommand

data class MarkItemSeenCommand(
    val itemId : String
)

data class SetRepHeroCommand(
    val heroId : String,
    val slotIdx : Int
)