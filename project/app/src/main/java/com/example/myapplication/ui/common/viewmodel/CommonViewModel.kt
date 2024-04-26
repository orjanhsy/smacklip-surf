package com.example.myapplication.ui.common.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.smackLip.SmackLipRepositoryImpl
import com.example.myapplication.model.surfareas.SurfArea
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class OFLFState(
    val dataFor7Days: Map<SurfArea, List<Map<List<Int>, List<Any>>>> = mapOf()
)

class CommonViewModel : ViewModel(){
    private val smackLipRepository = SmackLipRepositoryImpl()
    private val _oflfState = MutableStateFlow(OFLFState())
    val oflfState: StateFlow<OFLFState> = _oflfState.asStateFlow()

    init {
     getData7Days()
    }

    fun getData7Days(){
        viewModelScope.launch(Dispatchers.IO) {
            val updated7Days :Map<SurfArea, List<Map<List<Int>, List<Any>>>> = smackLipRepository.asyncCalls()
            _oflfState.update {
                it.copy(dataFor7Days = updated7Days)
            }
        }

    }


}