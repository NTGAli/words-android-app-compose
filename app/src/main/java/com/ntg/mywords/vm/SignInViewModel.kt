package com.ntg.mywords.vm

import androidx.lifecycle.ViewModel
import com.ntg.mywords.model.SignInResult
import com.ntg.mywords.model.sign.SignInState
import com.ntg.mywords.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignResult(result: SignInResult){
        timber("awljdlkawjdljlkwajdwljdlkwj oooo ::: ${result.errorMessage}")
        _state.update { it.copy(
            isSignSuccessful = result.data != null,
            signInError = result.errorMessage
        )
        }
    }

    fun resetSate(){
        _state.update { SignInState() }
    }
}