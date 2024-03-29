package com.ntg.vocabs.model

sealed class Result<T>
data class Success<T>(val value: T): Result<T>()
data class Failure<T>(val errorMessage: String): Result<T>()

infix fun <T,U> Result<T>.then(f: (T) -> Result<U>) =
    when (this) {
        is Success -> f(this.value)
        is Failure -> Failure(this.errorMessage)
    }