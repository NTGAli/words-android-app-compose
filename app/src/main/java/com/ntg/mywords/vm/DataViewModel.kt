package com.ntg.mywords.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.model.response.DataRes
import com.ntg.mywords.model.response.ResponseBody
import com.ntg.mywords.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val api: ApiService,
): ViewModel() {


    private var dataList: MutableLiveData<NetworkResult<ResponseBody<List<DataRes>?>>> = MutableLiveData()

    fun getDataList(lang: String): MutableLiveData<NetworkResult<ResponseBody<List<DataRes>?>>> {
        viewModelScope.launch {
            dataList = safeApiCall(Dispatchers.IO){
                api.germanDataList(lang)
            } as MutableLiveData<NetworkResult<ResponseBody<List<DataRes>?>>>
        }
        return dataList
    }

}