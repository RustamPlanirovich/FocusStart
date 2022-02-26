package com.example.focusstart

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusstart.retrofit.Repository
import com.example.focusstart.retrofit.model.Rate
import com.example.focusstart.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.util.*
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class ViewModel @Inject constructor(private val repository: Repository):ViewModel() {
    private val _state = MutableStateFlow<State<Rate>>(State.Waiting)
    val state:StateFlow<State<Rate>> get() = _state.asStateFlow()
    private  var timer:Timer? = null
    init {
        getRates()
    }

    fun getRates(){
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val answer = repository.getRates().awaitResponse()
                if(answer.isSuccessful && answer.code() == 200) //Success code
                    _state.value = State.Success(answer.body()!!)
                else
                    _state.value = State.Error(Exception("Unknown error"))
            }catch (e: Exception){
                _state.value = State.Error(e)
            }
        }
    }

    fun startAutoUpdate(){
        stopAutoUpdate()
        val periodTime:Long = 60*1000 // 1 minit
        timer = Timer()
        timer?.schedule(CustomTask(), 1000, 3000)
    }

    fun stopAutoUpdate(){
        timer?.let {
            it.cancel()
            timer = null
        }
        Log.d("TAG", "STOPPED")
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoUpdate()
    }

    inner class CustomTask:TimerTask() {
        override fun run() {
            getRates()
        }
    }
}