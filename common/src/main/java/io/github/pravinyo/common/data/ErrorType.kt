package io.github.pravinyo.common.data

sealed class ErrorType {
    abstract val message: String
}

data class IOError( override val message: String) : ErrorType()
data class ProcessError(override val message: String) : ErrorType()
data class UserAbort(override val message: String) : ErrorType()
data class InputError(override val message: String) : ErrorType()
data class UnknownError(override val message: String) : ErrorType()
