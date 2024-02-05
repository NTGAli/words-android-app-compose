package com.ntg.vocabs.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.model.response.RecentMessage
import com.ntg.vocabs.model.response.ResponseBody
import com.ntg.vocabs.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageBoxViewModel @Inject constructor(
    private val api: ApiService,
): ViewModel() {

    private var recentMessages: MutableLiveData<NetworkResult<ResponseBody<List<RecentMessage>?>>> = MutableLiveData()

    fun getMessages(): MutableLiveData<NetworkResult<ResponseBody<List<RecentMessage>?>>> {
        viewModelScope.launch {
            recentMessages = safeApiCall(Dispatchers.IO){
                api.recentMessages()
            } as MutableLiveData<NetworkResult<ResponseBody<List<RecentMessage>?>>>
        }
        return recentMessages
    }


}