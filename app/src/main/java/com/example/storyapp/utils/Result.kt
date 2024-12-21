package com.example.storyapp.utils


sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Failure(val error: Throwable) : Result<Nothing>()
    data object Loading : Result<Nothing>()

    companion object {
        fun <T> success(data: T): Result<T> = Success(data)
        fun failure(error: Throwable): Result<Nothing> = Failure(error)
        fun loading(): Result<Nothing> = Loading
    }
}
