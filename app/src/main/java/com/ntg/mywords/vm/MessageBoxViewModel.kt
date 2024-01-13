package com.ntg.mywords.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.model.response.RecentMessage
import com.ntg.mywords.model.response.ResponseBody
import com.ntg.mywords.model.response.WordVocab
import com.ntg.mywords.model.sign.SignInState
import com.ntg.mywords.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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