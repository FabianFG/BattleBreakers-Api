package me.fungames.battlebreakers.api.exceptions

import me.fungames.battlebreakers.api.model.EpicError


class EpicErrorException : Exception {
    constructor(error: EpicError) : super(error.errorMessage)
    constructor(e: Exception) : super(e)
    constructor(message : String) : super(message)
}