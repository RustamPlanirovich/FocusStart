package com.example.focusstart.retrofit.model

import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import javax.inject.Inject

class RateDeserialazer @Inject constructor(): JsonDeserializer<Rate> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Rate? {
        val jsonObject = json?.asJsonObject
        return jsonObject?.let {
            val date = it.get("Date").asString
            val prevDate = it.get("PreviousDate").asString
            val prevUrl = it.get("PreviousURL").asString
            val timestamp = it.get("Timestamp").asString
            val currency = it.get("Valute").asJsonObject
            val gson = Gson()
            val currencyList = mutableListOf<Currency>()
            currency.entrySet().forEach{ mask->
                val value = gson.fromJson(mask.value.toString(), Currency::class.java)
                currencyList += value
            }
            Rate(date, prevDate, prevUrl, timestamp, currencyList)
        }
    }
}