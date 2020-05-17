package com.example.androidtest.rest

import com.example.androidtest.models.ApiResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RestApi {

    @GET("api/android/latest/")
    fun getConversionData(@Query("base") base: String):
            Single<Response<ApiResponse>>
}