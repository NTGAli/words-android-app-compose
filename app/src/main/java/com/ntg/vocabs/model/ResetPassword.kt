package com.ntg.vocabs.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class ResetPassword(
    val id: String,
    val email: String,
    val code: Int,
    val time: String,
)
