package com.ntg.mywords.model.sign

data class SignInState(
    val isSignSuccessful: Boolean = false,
    val signInError: String? = null,
)
