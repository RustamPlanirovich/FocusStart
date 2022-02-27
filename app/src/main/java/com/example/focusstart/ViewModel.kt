package com.example.focusstart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.focusstart.retrofit.Repository
import com.example.focusstart.retrofit.model.Currency
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

@HiltViewModel
class ViewModel @Inject constructor(private val repository: Repository):ViewModel() {
    private val _state = MutableStateFlow<State<Rate>>(State.Waiting)
    val state:StateFlow<State<Rate>> get() = _state.asStateFlow()
    private  var timer: Timer? = null
    private var _currency = MutableLiveData<Currency?>(null)
    val currency:LiveData<Currency?> get() = _currency
    init {
        getRates()
    }

    fun getRates(){
        viewModelScope.launch {
            _state.value = State.Loading
            try {
                val answer = repository.getRates().awaitResponse()
                if(answer.isSuccessful && answer.code() == 200){        //Success code
                    val rate = answer.body()!!
                    rate.currency.find { it.id == currency.value?.id }?.let {
                        _currency.value = it
                    }
                    _state.value = State.Success(rate)
                }
                else
                    _state.value = State.Error(Exception("Unknown error"))
            }catch (e: Exception){
                _state.value = State.Error(e)
            }
        }
    }

    fun startAutoUpdate(){
        stopAutoUpdate()
        val periodTime:Long = 60*1000 // 1 minute
        timer = Timer()
        timer?.schedule(CustomTask(), 1000, periodTime)
    }

    fun stopAutoUpdate(){
        timer?.let {
            it.cancel()
            timer = null
        }
    }

    fun evaluateValue(value: String):String{
        return try {
                currency.value?.let {
                    val d = (value.toDouble() * it.nominal) / it.value
                    String.format("%.4f", d)
                } ?: "0.0"
        }catch (e: NumberFormatException){
            "0.0"
        }
    }

    fun setCurrency(curr: Currency) {
        _currency.value = curr
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoUpdate()
    }

    private inner class CustomTask:TimerTask() {
        override fun run() {
            getRates()
        }
    }
}