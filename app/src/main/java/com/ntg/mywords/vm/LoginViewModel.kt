package com.ntg.mywords.vm

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ntg.mywords.BuildConfig
import com.ntg.mywords.UserDataAndSetting
import com.ntg.mywords.api.ApiService
import com.ntg.mywords.api.NetworkResult
import com.ntg.mywords.di.DataRepository
import com.ntg.mywords.model.response.ResponseBody
import com.ntg.mywords.model.response.VerifyUserRes
import com.ntg.mywords.util.UserStore
import com.ntg.mywords.util.dataStore
import com.ntg.mywords.util.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: ApiService,
    private val dataRepository: DataRepository,
    private val userStore: UserStore
): ViewModel() {

    private var verifyUser: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var verifyCode: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var verifyPass: MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>> = MutableLiveData()
    private var verifyGoogle: MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>> = MutableLiveData()
    private var updateName: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var deleteAccountStatus: MutableLiveData<NetworkResult<ResponseBody<Nothing>>> = MutableLiveData()
    private var updateEmail: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private lateinit var userDataSettings: Flow<UserDataAndSetting>
    private var verifyByEmail: MutableLiveData<NetworkResult<ResponseBody<Nothing>>> = MutableLiveData()





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


    fun loginWithEmail(
        email: String
    ): MutableLiveData<NetworkResult<ResponseBody<Nothing>>> {
        viewModelScope.launch {
            verifyByEmail = safeApiCall(Dispatchers.IO){
                api.loginWithEmail(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email
                )
            } as MutableLiveData<NetworkResult<ResponseBody<Nothing>>>
        }
        return verifyByEmail
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
    ): MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>> {
        viewModelScope.launch {
            verifyPass = safeApiCall(Dispatchers.IO){
                api.verifyPassword(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email,
                    password = password
                )
            } as MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>>
        }
        return verifyPass
    }


    fun verifyUserByGoogle(
        email: String,
        username: String?,
        userId: String?
    ): MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>> {
        viewModelScope.launch {
            verifyGoogle = safeApiCall(Dispatchers.IO){
                api.verifyByGoogle(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email,
                    userId = userId,
                    name = username
                )
            } as MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>>
        }
        return verifyGoogle
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

    fun updateEmail(
        email: String,
        newEmail: String,
    ): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            updateEmail = safeApiCall(Dispatchers.IO){
                api.updateEmail(
                    token = BuildConfig.VOCAB_API_KEY,
                    currentEmail = email,
                    newEmail = newEmail
                )
            } as MutableLiveData<NetworkResult<String>>
        }
        return updateEmail
    }

    fun deleteAccount(
        email: String,
        password: String
    ): MutableLiveData<NetworkResult<ResponseBody<Nothing>>> {
        viewModelScope.launch {
            deleteAccountStatus = safeApiCall(Dispatchers.IO){
                api.deleteAccount(
                    token = BuildConfig.VOCAB_API_KEY,
                    email = email,
                    password = password
                )
            } as MutableLiveData<NetworkResult<ResponseBody<Nothing>>>
        }
        return deleteAccountStatus
    }

    fun setUserEmail(email: String) = viewModelScope.launch {
        dataRepository.setUserEmail(email)
    }

    fun setSkipLogin(skip: Boolean) = viewModelScope.launch {
        dataRepository.isSkipped(skip)
    }

    fun setUsername(name: String) = viewModelScope.launch {
        dataRepository.setUsername(name)
    }

    fun getUserData(): Flow<UserDataAndSetting> {
        viewModelScope.launch {
            userDataSettings = dataRepository.getUserData()
        }
        return userDataSettings
    }

    fun clearUserData() = viewModelScope.launch { dataRepository.clearAllUserData() }

    fun setTheme(theme: String){
        viewModelScope.launch {
            userStore.saveToken(theme)
        }
    }

    fun getTheme() = userStore.getAccessToken.asLiveData()


}