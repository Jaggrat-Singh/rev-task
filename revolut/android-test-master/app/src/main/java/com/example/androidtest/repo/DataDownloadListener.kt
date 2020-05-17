package com.example.androidtest.repo

import com.example.androidtest.models.ApiResponse

interface DataDownloadListener {
    fun onSuccess(list: ApiResponse?)
    fun onFailure (message: String)
}