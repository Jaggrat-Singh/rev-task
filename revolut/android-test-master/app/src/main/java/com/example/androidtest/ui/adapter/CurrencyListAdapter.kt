package com.example.androidtest.ui.adapter

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.androidtest.R
import com.example.androidtest.models.CurrencyInformation
import com.example.androidtest.models.ExchangeModel
import com.example.androidtest.ui.OnCurrencyTappedListener

class CurrencyListAdapter(private val listener: OnCurrencyTappedListener, private val textWatcher: TextWatcher) : RecyclerView.Adapter<CurrencyListAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyName: TextView = itemView.findViewById(R.id.tv_currency_name)
        val totalValue: EditText = itemView.findViewById(R.id.edt_total_value)
    }

    private val currencies = mutableListOf<CurrencyInformation>()
    private var userSelectedCurrency: CurrencyInformation? = null

    override fun getItemCount(): Int = currencies.size

    fun setConversionData(exchange: ExchangeModel) {
        exchange.let {
            currencies.clear()
            currencies.addAll(it.currencies)
            userSelectedCurrency = it.baseCurrency
            if (it.isBaseCurrencyUpdated) {
                notifyDataSetChanged()
            } else {
                notifyItemRangeChanged(1, currencies.size)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currencyInformation: CurrencyInformation = currencies[position]
        holder.currencyName.text = currencyInformation.name

        if (userSelectedCurrency == currencyInformation && position == 0) {
            holder.totalValue.addTextChangedListener(textWatcher)
            if(!holder.totalValue.text.toString().equals(currencyInformation.totalValue)) {
                holder.totalValue.setText(currencyInformation.totalValue)
            }
        } else {
            holder.totalValue.removeTextChangedListener(textWatcher)
            holder.totalValue.setText(currencyInformation.totalValue)
            holder.itemView.setOnClickListener { listener.onItemTapped(currencyInformation) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ViewHolder(v)
    }
}