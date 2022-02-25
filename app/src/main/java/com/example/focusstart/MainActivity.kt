package com.example.focusstart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.focusstart.databinding.ActivityMainBinding
import com.example.focusstart.util.State
import com.example.focusstart.util.launchWhenStarted
import com.example.focusstart.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.onEach
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ViewModel by viewModels()
    private lateinit var _adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setAdapter()
        setViewModel()
    }

    private fun setAdapter(){
        _adapter = Adapter()
        with(binding.recycler){
            layoutManager = LinearLayoutManager(context)
            adapter = _adapter
        }
    }

    private fun setViewModel() {
        viewModel.state.onEach {state ->
            when(state){
                is State.Loading ->{

                }
                is State.Error ->{
                    state.e.message?.showToast(this)
                }
                is State.Success ->{
                    _adapter.submitList(state.result.currency)

                    val d = state.result.date.substring(0, state.result.date.length-6)
                    Log.d("TAG", "TIME: $d")
                    val form = SimpleDateFormat("yyyy")
                    val loc = form.parse(d)
                    Log.d("TAG", "loc-> $loc")

                }
            }
        }.launchWhenStarted(lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}