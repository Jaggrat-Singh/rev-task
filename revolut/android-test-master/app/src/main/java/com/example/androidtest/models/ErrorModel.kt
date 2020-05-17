package com.example.androidtest.models

sealed class ErrorState{
    object Warning: ErrorState()
    object Error: ErrorState()
}