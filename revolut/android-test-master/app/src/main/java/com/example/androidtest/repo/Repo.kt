package com.example.androidtest.repo

import io.reactivex.disposables.Disposable

interface Repo {

    fun getConversionData(listener: DataDownloadListener?, currencyName: String): Disposable
}