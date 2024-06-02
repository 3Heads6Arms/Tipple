package com.anhhoang.tipple.core.data.model

/** A generic class that holds a value with its loading status. */
sealed class Resource<out T> {
    /** Loading state. */
    data object Loading : Resource<Nothing>()

    /** Success state with data. */
    data class Success<T>(val data: T) : Resource<T>()

    /** Error state with exception. */
    data class Error(val throwable: Throwable? = null) : Resource<Nothing>()
}