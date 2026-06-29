package com.reset.core.network

/**
 * Result of a data-layer call, mirroring `in.mohalla.core.network.ApiResult` from the
 * reference. UseCases return this; ViewModels branch on [Success] / [Error].
 */
sealed interface ApiResult<out T> {

    data class Success<out T>(val data: T) : ApiResult<T>

    data class Error(val exception: Throwable? = null) : ApiResult<Nothing>
}

inline fun <T> ApiResult<T>.onSuccess(block: (T) -> Unit): ApiResult<T> {
    if (this is ApiResult.Success) block(data)
    return this
}

inline fun <T> ApiResult<T>.onError(block: (Throwable?) -> Unit): ApiResult<T> {
    if (this is ApiResult.Error) block(exception)
    return this
}
