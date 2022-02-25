package com.example.focusstart.retrofit

import javax.inject.Inject

class Repository @Inject constructor(private val service: RateService){
    fun getRates() = service.getRates()
}