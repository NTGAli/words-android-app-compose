package com.ntg.vocabs.vm

import androidx.lifecycle.ViewModel
import com.ntg.vocabs.model.SignInResult
import com.ntg.vocabs.model.sign.SignInState
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


class SignInViewModel: ViewModel() {

//    private val _state = MutableStateFlow(SignInState())
//    val state = _state.asStateFlow()
//
//    fun onSignResult(result: SignInResult){
//        timber("awljdlkawjdljlkwajdwljdlkwj oooo ::: ${result.errorMessage}")
//        _state.update { it.copy(
//            isSignSuccessful = result.data != null,
//            signInError = result.errorMessage
//        )
//        }
//    }
//
//    fun resetSate(){
//        _state.update { SignInState() }
//    }
}