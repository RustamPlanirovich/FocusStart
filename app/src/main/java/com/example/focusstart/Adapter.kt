package com.example.focusstart

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.focusstart.databinding.CurrencyItemBinding
import com.example.focusstart.retrofit.model.Currency

class Adapter:ListAdapter<Currency, Adapter.CustomViewHolder>(DiffCallback) {

    override fun getItemViewType(position: Int): Int {
        return if(position == 0) 0  else 1
    }
    class CustomViewHolder(
        private val view: View
        ):RecyclerView.ViewHolder(view){
            fun bind(item: Currency){
                val binding: CurrencyItemBinding = DataBindingUtil.bind(view)!!
                binding.currency = item
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val binding = CurrencyItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CustomViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Currency>(){
            override fun areItemsTheSame(oldItem: Currency, newItem: Currency): Boolean {
                return oldItem.value == newItem.value
            }

            override fun areContentsTheSame(oldItem: Currency, newItem: Currency): Boolean {
                return oldItem == newItem
            }

        }
    }
}