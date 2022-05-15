package com.example.testapp.utils

sealed class Results<out T:Any>{
    data class Success<out T:Any>(val data:T):Results<T>()
    data class Error(val error: String):Results<Nothing>()
}
