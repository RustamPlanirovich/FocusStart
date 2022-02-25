package com.example.focusstart.util

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

fun CharSequence.showToast(context: Context){
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun <T> Flow<T>.launchWhenStarted(lifecycleScope: LifecycleCoroutineScope){
    lifecycleScope.launchWhenStarted {
        this@launchWhenStarted.collect()
    }
}
