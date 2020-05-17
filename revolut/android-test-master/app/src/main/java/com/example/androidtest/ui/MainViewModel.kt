package com.example.androidtest.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.androidtest.models.*
import com.example.androidtest.repo.DataDownloadListener
import com.example.androidtest.repo.Repo
import com.example.androidtest.utils.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit

import javax.inject.Inject

class MainViewModel @Inject constructor(private val repo: Repo, private val schedulerProvider: SchedulerProvider) : ViewModel() {
    val exchangeModel: MutableLiveData<ExchangeModel> = MutableLiveData()
    val errorModel: MutableLiveData<ErrorState> = MutableLiveData()
    private val disposableList =  CompositeDisposable()
    private lateinit var currencyFormat: String

    internal companion object {
        const val REFRESH_TIME_IN_SEC: Long = 1 //TODO[Future Updates]: this should also come from server to change value dynamically.. and avoid server burns...
        const val DEFAULT_TOTAL_MIN_VALUE: Double = 1.00 //TODO[Future Updates]: this could come from server to set minimum transferable value for particular currency.
    }

    /**
     * Calls repo to get latest data.
     * @param name: Currency name
     * @param value: Total value.
     * @param format: Data format.
     */
    fun pollConversionData(name: String, value: String, format: String) {
        exchangeModel.value = ExchangeModel(currencies = emptyList(), baseCurrency = CurrencyInformation(name, String.format(format, value.toDouble())), isBaseCurrencyUpdated = false)
        currencyFormat = format
        pollCurrencyData()
    }

    private fun pollCurrencyData() {
        val disposable = Observable.interval(REFRESH_TIME_IN_SEC, TimeUnit.SECONDS, schedulerProvider.io)
            .subscribeOn(schedulerProvider.io)
            .observeOn(schedulerProvider.main)
            .subscribe ({
                disposableList.add(repo.getConversionData(listener, getBaseCurrency().name))
            }, {
                errorModel.value = ErrorState.Warning
                //TODO[Future Updates]: Pass error to product monitoring server.
            })
        disposableList.add(disposable)
    }


    var listener : DataDownloadListener? = object : DataDownloadListener {
        override fun onFailure(message: String) {
            exchangeModel.value = null
            errorModel.value = ErrorState.Error
            //TODO[Future Updates]: pass each error to monitoring server ... an alarm should go off if error goes beyond threshold.
        }

        override fun onSuccess(list: ApiResponse?) {
            list?.let {
                processConversionDataIntoInfo(it)
            }
        }
    }

    /**
     * Updates total value for current currency and updates other currency conversions.
     * @param totalValue: Total value for selected currency
     */
    fun updateTotalValue(totalValue: String) {
        val updatedCurrencyName = getBaseCurrency().name
        val updatedTotalValue = totalValue.toDouble().coerceAtLeast(DEFAULT_TOTAL_MIN_VALUE)
        exchangeModel.value = exchangeModel.value!!.copy(baseCurrency = CurrencyInformation(updatedCurrencyName, String.format(currencyFormat, updatedTotalValue)), isBaseCurrencyUpdated = false)
        //TODO[Future Updates]: See if user has entered value too high or too low...
        //TODO[Future Updates]: pass this value to backend for user behaviour analysis..may be we can analysis how many times user has gone beyond transfer limit, if any.
    }

    /**
     * Sets the selected currency as a base currency.
     * @param currency: New selected Currency
     */
    fun onCurrencySelected(currency: CurrencyInformation) {
        disposableList.clear()
        exchangeModel.value = exchangeModel.value!!.copy(baseCurrency = currency, isBaseCurrencyUpdated = true)
        pollCurrencyData()
        //TODO[Future Updates]: Pass selected currency to backend to see user behaviour. May be we can see our HOT currency
    }

    private fun processConversionDataIntoInfo(response: ApiResponse) {
        val currencies = arrayListOf<CurrencyInformation>()
        currencies.add(getBaseCurrency())
        response.rates.map {
            if (getBaseCurrency().name != it.key) {
                val formattedTotalValue = String.format(
                    currencyFormat,
                    getBaseCurrency().totalValue.toDouble() * it.value
                )
                currencies.add(CurrencyInformation(it.key, formattedTotalValue))
            }
        }
        exchangeModel.value = exchangeModel.value!!.copy(currencies = currencies)
    }


    public override fun onCleared() {
        super.onCleared()
        listener = null
        disposableList.dispose()
    }

    private fun getBaseCurrency(): CurrencyInformation = exchangeModel.value!!.baseCurrency
}