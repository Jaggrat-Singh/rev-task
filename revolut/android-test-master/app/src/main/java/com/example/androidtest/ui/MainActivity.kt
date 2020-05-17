package com.example.androidtest.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtest.R
import com.example.androidtest.di.Injectable
import com.example.androidtest.models.CurrencyInformation
import com.example.androidtest.models.ErrorState
import com.example.androidtest.ui.adapter.CurrencyListAdapter
import com.example.androidtest.utils.verifyAvailableNetwork
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable, OnCurrencyTappedListener {

    @Inject
    internal lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel

    private val currencyTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence,
            start: Int,
            count: Int,
            after: Int
        ) { // No action
        }

        override fun onTextChanged(
            s: CharSequence,
            start: Int,
            before: Int,
            count: Int
        ) {
            onTotalValueChanged(s)
        }

        override fun afterTextChanged(s: Editable) { // No action
        }
    }

    private val adapter = CurrencyListAdapter(this, currencyTextWatcher)


    private fun onTotalValueChanged(totalValue: CharSequence) {
        viewModel.updateTotalValue(totalValue = totalValue.toString())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        setupView()
        observeConversionRates()
        observeErrors()
        initValue()
    }

    private fun initValue() {
        if (verifyAvailableNetwork(this)) {
            showProgressing()
            viewModel.pollConversionData(getString(R.string.base_currency), getString(R.string.base_value), getString(R.string.currency_format))
        } else {
            Toast.makeText(this, getString(R.string.internet_warning), Toast.LENGTH_LONG).show()
        }
    }


    private fun observeConversionRates() {
        viewModel.exchangeModel.observe(this, Observer { model ->
            if (model != null) {
                Log.d("jaggrat activit", Thread.currentThread().name)
                recycler_view.post {
                    adapter.setConversionData(model)
                }
            } else {
                Toast.makeText(this, getString(R.string.data_error), Toast.LENGTH_LONG).show()
            }
            dismissProgressing()
        })
    }

    private fun observeErrors() {
        viewModel.errorModel.observe(this, Observer { error ->
           when (error) {
               ErrorState.Warning -> {
                   recycler_view.visibility = View.GONE
                   tv_error.text = getString(R.string.data_error)
               }

               ErrorState.Error -> {
                   recycler_view.visibility = View.GONE
                   tv_error.text = getString(R.string.api_down)
               }
           }
            dismissProgressing()
        })
    }
    /**
     * Sets up views
     */
    private fun setupView() {
        recycler_view.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = adapter
            it.addItemDecoration(DividerItemDecoration(it.context, RecyclerView.VERTICAL))
        }
    }

    /**
     * Show progress
     */
    private fun showProgressing() {
        LoadingDialogFragment().show(supportFragmentManager, LoadingDialogFragment.TAG)
    }

    /**
     * Dismiss progress
     */
    private fun dismissProgressing() {
        supportFragmentManager.findFragmentByTag(LoadingDialogFragment.TAG)
            ?.takeIf { it is DialogFragment }
            ?.run {
                (this as DialogFragment).dismiss()
            }
    }


    override fun onItemTapped(item: CurrencyInformation) {
        viewModel.onCurrencySelected(item)
    }
}
