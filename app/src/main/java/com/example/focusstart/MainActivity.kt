package com.example.focusstart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focusstart.databinding.ActivityMainBinding
import com.example.focusstart.util.State
import com.example.focusstart.util.launchWhenStarted
import com.example.focusstart.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*

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
    }

    private fun setAdapter(){
        _adapter = Adapter()
        with(binding){
            recycler.layoutManager = LinearLayoutManager(this@MainActivity)
            recycler.adapter = _adapter
            swipeLayout.setOnRefreshListener {
                viewModel.getRates()
            }
        }
    }

    private fun setViewModel() {
        viewModel.state.onEach {state ->
            when(state){
                is State.Loading ->{
                    binding.swipeLayout.isRefreshing = true
                }
                is State.Error ->{
                    binding.swipeLayout.isRefreshing = false
                    state.e.message?.showToast(this)
                }
                is State.Success ->{
                    binding.swipeLayout.isRefreshing = false
                    _adapter.submitList(state.result.currency)
                    state.result.also {
                        binding.curDate = it.date
                        binding.prevDate = it.previousDate
                        binding.timeStamp = it.timestamp
                    }
                    getString(R.string.update_success).showToast(this)
                }
            }
        }.launchWhenStarted(lifecycleScope)

        CoroutineScope(Dispatchers.IO).launch {
            delay(3000)
            viewModel.startAutoUpdate()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}