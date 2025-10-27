package surik.simyan.locdots.server.api.exceptions

open class ApiException(
    val code: String,
    override val message: String,
) : RuntimeException(message)

class NotFoundException(
    message: String,
) : ApiException("NOT_FOUND", message)

class ValidationException(
    message: String,
) : ApiException("VALIDATION_ERROR", message)

class UnauthorizedException(
    message: String,
) : ApiException("UNAUTHORIZED", message)
