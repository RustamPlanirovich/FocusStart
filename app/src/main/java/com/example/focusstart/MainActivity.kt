package com.example.focusstart

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focusstart.databinding.ActivityMainBinding
import com.example.focusstart.util.State
import com.example.focusstart.util.launchWhenStarted
import com.example.focusstart.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewModel by viewModels()
    private lateinit var _adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)
        setAdapter()
        setViewModel()
        setView()
    }

    override fun onStart() {
        super.onStart()
        if(binding.autoUpdateCHB.isChecked)
            viewModel.startAutoUpdate()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopAutoUpdate()
    }

    private fun setView() {
        binding.autoUpdateCHB.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                viewModel.startAutoUpdate()
            } else
                viewModel.stopAutoUpdate()
        }
        binding.valueET.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                updateConvertedValue()
                true
            } else
                false
        }
        //verify checkbox preferences (on/off)
        val isChecked = this.getPreferences(Context.MODE_PRIVATE).getBoolean(
            getString(R.string.checkbox_pref), false
        )
        binding.autoUpdateCHB.isChecked = isChecked
    }

    private fun setAdapter() {
        _adapter = Adapter { currency ->
            viewModel.setCurrency(currency)
            updateConvertedValue()
        }
        with(binding) {
            recycler.layoutManager = LinearLayoutManager(this@MainActivity)
            recycler.adapter = _adapter
            swipeLayout.setOnRefreshListener {
                viewModel.getRates()
            }
        }
    }

    private fun updateConvertedValue() {
        binding.convertedTV.text = viewModel.evaluateValue(binding.valueET.text.toString())
    }

    private fun setViewModel() {
        viewModel.state.onEach { state ->
            when (state) {
                is State.Loading -> {
                    binding.swipeLayout.isRefreshing = true
                }
                is State.Error -> {
                    binding.swipeLayout.isRefreshing = false
                    state.e.message?.showToast(this)
                }
                is State.Success -> {
                    binding.swipeLayout.isRefreshing = false
                    _adapter.submitList(state.result.currency)
                    state.result.also {
                        binding.curDate = it.date
                        binding.prevDate = it.previousDate
                        binding.timeStamp = it.timestamp
                    }
//                    getString(R.string.update_success).showToast(this)
                }
                else -> {
                    //State.Waiting
                }
            }
        }.launchWhenStarted(lifecycleScope)
        viewModel.currency.observe(this) {
            binding.currentValue = it
            updateConvertedValue()
        }

    }

    override fun onPause() {
        super.onPause()
        val pref = this.getPreferences(Context.MODE_PRIVATE)
        with(pref.edit()){
            putBoolean(getString(R.string.checkbox_pref), binding.autoUpdateCHB.isChecked)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}