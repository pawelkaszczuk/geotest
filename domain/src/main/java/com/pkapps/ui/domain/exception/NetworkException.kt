package com.pkapps.ui.domain.exception

class NetworkException(private val exception: Throwable = Exception()) : Exception()