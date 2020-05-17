package com.example.androidtest.repo

import com.example.androidtest.rest.RestApi
import com.example.androidtest.utils.SchedulerProvider
import io.reactivex.disposables.Disposable

class RepoImpl(private val restApi: RestApi, private val schedulerProvider: SchedulerProvider) : Repo {

    override fun getConversionData(listener: DataDownloadListener?, currencyName: String): Disposable {
        return restApi.getConversionData(currencyName)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.main)
            .subscribe({ templates ->
                listener?.onSuccess(templates.body())
                //TODO : Right now this threading could seems redundant but in future we can use this threading to update database/cache etc.
            }, {
               listener?.onFailure(it.localizedMessage)
            })
    }
}