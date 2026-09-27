package com.streakly.data.healthconnect

sealed class HealthResult<out T> {
    data class Success<out T>(val data: T) : HealthResult<T>()
    data class Error(val exception: Throwable) : HealthResult<Nothing>()
    object Loading : HealthResult<Nothing>()
}
