package com.ntg.mywords.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.BuildConfig
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: ApiService
): ViewModel() {

    private var verifyUser: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var verifyCode: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var verifyPass: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var updateName: MutableLiveData<NetworkResult<String>> = MutableLiveData()


    fun sendCode(
        email: String
    ): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            verifyUser = safeApiCall(Dispatchers.IO){
                api.verifyUser(
                    /**
                     * read [ApiService] to set [BuildConfig.VOCAB_API_KEY]
                     * */
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email
                )
            } as MutableLiveData<NetworkResult<String>>
        }
        return verifyUser
    }

    fun verifyUserByCode(
        email: String,
        code: String
    ): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            verifyCode = safeApiCall(Dispatchers.IO){
                api.verifyCode(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email,
                    code = code
                )
            } as MutableLiveData<NetworkResult<String>>
        }
        return verifyCode
    }


    fun verifyUserByPassword(
        email: String,
        password: String
    ): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            verifyPass = safeApiCall(Dispatchers.IO){
                api.verifyPassword(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email,
                    password = password
                )
            } as MutableLiveData<NetworkResult<String>>
        }
        return verifyPass
    }


    fun updateName(
        email: String,
        name: String
    ): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            updateName = safeApiCall(Dispatchers.IO){
                api.updateName(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email,
                    name = name
                )
            } as MutableLiveData<NetworkResult<String>>
        }
        return updateName
    }


}