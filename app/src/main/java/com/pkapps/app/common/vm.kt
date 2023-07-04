package com.pkapps.app.common

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

inline fun <reified T, reified E : T> MutableStateFlow<T>.updateAs(function: (E) -> T) {
    update { value ->
        if (value is E) {
            function(value)
        } else {
            value
        }
    }
}