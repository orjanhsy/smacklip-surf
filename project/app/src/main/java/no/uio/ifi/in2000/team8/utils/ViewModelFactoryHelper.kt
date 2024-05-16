package no.uio.ifi.in2000.team8.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// use to instantiate viewModels with parameters
fun <VM: ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object: ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return initializer() as T
        }
    }
}