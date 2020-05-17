package com.example.androidtest.models

data class ExchangeModel(
    var currencies: List<CurrencyInformation>,
    var baseCurrency: CurrencyInformation,
    var isBaseCurrencyUpdated: Boolean
)