package com.example.androidtest.models

data class ApiResponse(
    val baseCurrency: String,
    val rates: HashMap<String, Double>
)