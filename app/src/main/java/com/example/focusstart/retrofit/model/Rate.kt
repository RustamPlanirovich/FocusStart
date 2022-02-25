package com.example.focusstart.retrofit.model

data class Rate(
    val date: String,
    val previousDate: String,
    val previousURL: String,
    val timestamp: String,
    val currency: List<Currency>
)