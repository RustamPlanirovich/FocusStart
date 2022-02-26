package com.example.focusstart.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

fun CharSequence.showToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope){
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}

fun String.parseDate(): String{
    return try {
        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val formatter = SimpleDateFormat("HH:mm:ss MMM dd yyyy", Locale.getDefault())
        formatter.format(parser.parse(this))
    }catch (e: ParseException){
        LocalDateTime.now().toString()
    }
}
