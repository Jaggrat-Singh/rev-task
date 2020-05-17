package com.example.androidtest.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.androidtest.models.ApiResponse
import com.example.androidtest.models.CurrencyInformation
import com.example.androidtest.models.ErrorState
import com.example.androidtest.repo.Repo
import com.example.androidtest.utils.SchedulerProvider
import com.nhaarman.mockito_kotlin.*
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.TestScheduler
import org.junit.*

class MainViewModelTest {

    private lateinit var repo: Repo
    private lateinit var viewModel : MainViewModel

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val scheduler = TestScheduler()
    private val testSchedulerProvider = SchedulerProvider(Schedulers.trampoline(), scheduler, Schedulers.trampoline())

    @Before
    fun setUp() {
        repo = mock()
        viewModel = MainViewModel(repo, testSchedulerProvider)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `When user is on screen then it should have called repo to get data and sets base currency`() {
        viewModel.pollConversionData("EUR", "100.00", "%.2f")

        verify(repo, atLeast(1)).getConversionData(viewModel.listener, "EUR")
        Assert.assertEquals(viewModel.exchangeModel.value!!.baseCurrency.name, "EUR")
        Assert.assertEquals(viewModel.exchangeModel.value!!.baseCurrency.totalValue, "100.00")
    }

    @Test
    fun `When user updates total value then it should be updated correctly in base currency`() {
        viewModel.pollConversionData("EUR", "100.00", "%.2f")
        viewModel.updateTotalValue("200")

        Assert.assertEquals(viewModel.exchangeModel.value!!.baseCurrency.name, "EUR")
        Assert.assertEquals(viewModel.exchangeModel.value!!.baseCurrency.totalValue, "200.00")
        Assert.assertEquals(viewModel.exchangeModel.value!!.isBaseCurrencyUpdated, false)
    }

    @Test
    fun `When user updates taps on any other currency then it should be updated correctly in base currency`() {
        viewModel.pollConversionData("EUR", "100.00", "%.2f")
        viewModel.onCurrencySelected(CurrencyInformation("INR", "88.00"))

        Assert.assertEquals(viewModel.exchangeModel.value!!.baseCurrency.name, "INR")
        Assert.assertEquals(viewModel.exchangeModel.value!!.baseCurrency.totalValue, "88.00")
        Assert.assertEquals(viewModel.exchangeModel.value!!.isBaseCurrencyUpdated, true)
    }

    @Test
    fun `When api response is mocked then exchangeModel should have correct value`() {
        viewModel.pollConversionData("EUR", "100.00", "%.2f")
        viewModel.listener!!.onSuccess(getMockResponse())

        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies.size, 4)
        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[0].name, "EUR")
        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[0].totalValue, "100.00")

        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[1].name, "BGN")
        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[1].totalValue, "175.98")

        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[2].name, "INR")
        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[2].totalValue, "8000.00")

        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[3].name, "GBP")
        Assert.assertEquals(viewModel.exchangeModel.value!!.currencies[3].totalValue, "115.12")
    }

    @Test
    fun `When user press back then disposable should be null`() {
        viewModel.pollConversionData("EUR", "100.00", "%.2f")

        viewModel.onCleared()

        Assert.assertNull(viewModel.listener)
    }

    @Test
    fun `When api fails then error state should be updated`() {
        viewModel.pollConversionData("EUR", "100.00", "%.2f")

        viewModel.listener!!.onFailure("API error")

        Assert.assertEquals(viewModel.errorModel.value, ErrorState.Error)

    }

    private fun getMockResponse(): ApiResponse {
        return ApiResponse("EUR", hashMapOf("INR" to 80.0 , "GBP" to 1.15123, "BGN" to 1.7598))
    }
}