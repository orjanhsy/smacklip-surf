package com.example.myapplication.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// use to instantiate viewModels with parameters
fun <VM: ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}