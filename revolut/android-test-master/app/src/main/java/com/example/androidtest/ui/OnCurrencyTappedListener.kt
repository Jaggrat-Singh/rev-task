package com.example.androidtest.ui

import com.example.androidtest.models.CurrencyInformation

interface OnCurrencyTappedListener {
    fun onItemTapped(item: CurrencyInformation)
}