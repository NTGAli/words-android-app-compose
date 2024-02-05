package com.ntg.vocabs.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.model.response.DataRes
import com.ntg.vocabs.model.response.ResponseBody
import com.ntg.vocabs.util.safeApiCall
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