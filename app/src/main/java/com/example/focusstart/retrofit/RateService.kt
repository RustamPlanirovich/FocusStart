package com.example.focusstart.retrofit

import com.example.focusstart.retrofit.model.Rate
import com.example.focusstart.util.Constants.TARGET
import retrofit2.Call
import retrofit2.http.GET

interface RateService {

    @GET(TARGET)
    fun getRates(): Call<Rate>
}