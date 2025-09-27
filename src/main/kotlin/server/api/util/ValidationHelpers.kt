package surik.simyan.locdots.server.api.util

import surik.simyan.locdots.server.api.exceptions.ValidationException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun requireParameter(value: Boolean, lazyMessage: () -> Any) {
    contract {
        returns() implies value
    }
    if (!value) {
        val message = lazyMessage()
        throw ValidationException(message.toString())
    }
}
