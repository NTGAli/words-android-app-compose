package com.ntg.vocabs.api.auth

import android.content.Intent
import android.content.IntentSender
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.api.services.drive.Drive
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.ntg.vocabs.util.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun loginUser(email: String, password: String): Flow<Resource<AuthResult>>
    fun registerUser(email: String, password: String): Flow<Resource<AuthResult>>

    fun googleSignIn(credential: AuthCredential): Flow<Resource<AuthResult>>
}
