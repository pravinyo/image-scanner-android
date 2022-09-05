package io.github.pravinyo.common.data

sealed class OperationState{
    object Idle : OperationState()
    object Processing : OperationState()
    object Success: OperationState()
    data class Failure(val reason: ErrorType) : OperationState()
}
