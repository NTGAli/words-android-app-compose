package com.ntg.vocabs.vm

import android.content.Intent
import android.content.IntentSender
import android.net.Uri
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.actionCodeSettings
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.ntg.vocabs.BuildConfig
import com.ntg.vocabs.UserDataAndSetting
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.api.auth.AuthRepository
import com.ntg.vocabs.di.DataRepository
import com.ntg.vocabs.model.GoogleSignInState
import com.ntg.vocabs.model.response.ResponseBody
import com.ntg.vocabs.model.response.VerifyUserRes
import com.ntg.vocabs.model.response.VipUser
import com.ntg.vocabs.model.sign.SignInState
import com.ntg.vocabs.util.Resource
import com.ntg.vocabs.util.UserStore
import com.ntg.vocabs.util.generateCode
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.safeApiCall
import com.ntg.vocabs.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: ApiService,
    private val dataRepository: DataRepository,
    private val userStore: UserStore,
    private val repository: AuthRepository,
    private val auth: FirebaseAuth,
    private val mFirestore: FirebaseFirestore
) : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var verifyUser: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var verifyCode: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var verifyPass: MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>> =
        MutableLiveData()
    private var verifyGoogle: MutableLiveData<NetworkResult<ResponseBody<VerifyUserRes>>> =
        MutableLiveData()
    private var updateName: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private var deleteAccountStatus: MutableLiveData<NetworkResult<ResponseBody<Nothing>>> =
        MutableLiveData()
    private var updateEmail: MutableLiveData<NetworkResult<String>> = MutableLiveData()
    private lateinit var userDataSettings: Flow<UserDataAndSetting>
    private var verifyByEmail: MutableLiveData<NetworkResult<ResponseBody<Nothing>>> =
        MutableLiveData()
    var allowDictionary: MutableLiveData<Boolean?> = MutableLiveData(null)

    fun sendCode(
        email: String
    ): MutableLiveData<NetworkResult<String>> {
        viewModelScope.launch {
            verifyUser = safeApiCall(Dispatchers.IO) {
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
            verifyByEmail = safeApiCall(Dispatchers.IO) {
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
            verifyCode = safeApiCall(Dispatchers.IO) {
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
            verifyPass = safeApiCall(Dispatchers.IO) {
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
            verifyGoogle = safeApiCall(Dispatchers.IO) {
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
            updateName = safeApiCall(Dispatchers.IO) {
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
            updateEmail = safeApiCall(Dispatchers.IO) {
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
            deleteAccountStatus = safeApiCall(Dispatchers.IO) {
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

    fun setReminderNotification(allow: Boolean) = viewModelScope.launch {
        dataRepository.allowNotificationReminder(allow)
    }

    fun setBackupOption(option: String) = viewModelScope.launch {
        dataRepository.setBackupOption(option)
    }

    fun setSkipLogin(skip: Boolean) = viewModelScope.launch {
        dataRepository.isSkipped(skip)
    }

    fun setFinishIntro(finished: Boolean) = viewModelScope.launch {
        dataRepository.isIntroFinished(finished)
    }

    fun setBackupWay(way: String) = viewModelScope.launch {
        dataRepository.setBackupWay(way)
    }

    fun checkBackup(setBackup: Boolean) = viewModelScope.launch {
        dataRepository.checkBackup(setBackup)
    }

    fun setPurchase(isSuccess: Boolean) = viewModelScope.launch {
        timber("setPurchase :::: $isSuccess")
        dataRepository.isUserPurchased(isSuccess)
    }

    fun setAllowDictionary(allow: Boolean) = viewModelScope.launch {
        dataRepository.isAllowThirdDictionary(allow)
    }

    fun setUsername(name: String) = viewModelScope.launch {
        if (name.contains("no one")){
            dataRepository.setUsername("no one")
        }else{
            dataRepository.setUsername(name)
        }
    }

    fun continueFree() = viewModelScope.launch {
        dataRepository.isSubscriptionSkipped(true)
    }

    fun getUserData(): Flow<UserDataAndSetting> {
        viewModelScope.launch {
            userDataSettings = dataRepository.getUserData()
        }
        return userDataSettings
    }

    fun clearUserData() = viewModelScope.launch { dataRepository.clearAllUserData() }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            userStore.saveTheme(theme)
        }
    }

    fun getTheme() = userStore.getTheme.asLiveData()

    val _signInState = Channel<SignInState>()
    val signInState = _signInState.receiveAsFlow()

    val _googleState = mutableStateOf(GoogleSignInState())
    val googleState: State<GoogleSignInState> = _googleState

    fun googleSignIn(credential: AuthCredential) = viewModelScope.launch {
        repository.googleSignIn(credential).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _googleState.value = GoogleSignInState(success = result.data)
                }

                is Resource.Loading -> {
                    _googleState.value = GoogleSignInState(loading = true)
                }

                is Resource.Error -> {
                    _googleState.value = GoogleSignInState(error = result.message!!)
                }
            }


        }
    }

    fun loginUser(email: String, password: String) = viewModelScope.launch {
        repository.loginUser(email, password).collect { result ->
            when (result) {
                is Resource.Success -> {
                    _signInState.send(SignInState(isSuccess = "Sign In Success "))
                }

                is Resource.Loading -> {
                    _signInState.send(SignInState(isLoading = true))
                }

                is Resource.Error -> {

                    _signInState.send(SignInState(isError = result.message))
                }
            }

        }
    }

    fun checkIfUserExists(
        email: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    val userExists = signInMethods?.isNotEmpty() ?: false
                    onSuccess(userExists)
                } else {
                    onFailure(task.exception ?: Exception("Error checking user existence"))
                }
            }
    }


    fun signUpEmailPassword(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (java.lang.Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setUserEmail(email)
                    onSuccess.invoke(true)
                } else {
                    task.exception?.let { onFailure.invoke(it) }
                }
            }
    }

    fun signIn(
        email: String,
        password: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (java.lang.Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    setUserEmail(email)
                    onSuccess.invoke(true)
                } else {
                    task.exception?.let { onFailure.invoke(it) }
                }
            }
    }

    fun sendLoginCode(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (java.lang.Exception) -> Unit
    ) {
        val actionCodeSettings = actionCodeSettings {
            url = "https://www.ntgt.ir/finishSignUp?cartId=1234"
            handleCodeInApp = true
            setAndroidPackageName(
                "com.ntg.vocabs",
                true,
                "1",
            )
        }
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess.invoke()
                    timber("EMAAAAAAAAAAAIL TRUE")
                } else {
                    timber("EMAAAAAAAAAAAIL ${task.exception?.message}")
                    task.exception?.let { onFailure.invoke(it) }
                }
            }
    }

    fun loginWithCode(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (java.lang.Exception) -> Unit
    ) {
        val resetPass = HashMap<String, Any>()
        resetPass["id"] = generateCode().toString()
        resetPass["email"] = email
        resetPass["code"] = generateCode()
        resetPass["time"] = System.currentTimeMillis().toString()
        mFirestore.collection("recoverPass").document("reset_passwords")
            .set(resetPass)
            .addOnSuccessListener {
                timber("DocumentSnapshot successfully written!")
                onSuccess.invoke()
            }
            .addOnFailureListener {
                timber("ERROR ${it.message}")
                onFailure.invoke(it)
            }
    }

    fun allowThirdDictionary() {
        val messagesRef = database.child("allowThirdDictionary")
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                allowDictionary.value = snapshot.getValue(Boolean::class.java) ?: false
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }

    private var isChecked = false
    fun checkIsVipUsers(userMail: String) {
        if (isChecked) return
        val messagesRef = database.child("vipUser")
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isChecked = true
                for (messageSnapshot in snapshot.children) {
                    val user = messageSnapshot.getValue(VipUser::class.java)
                    if (user != null) {
                        if (user.email != null && user.email.lowercase() == userMail.lowercase()){
                            timber("VipUser ::: $user --- $userMail --- ${user?.email} -- ${user?.pro} $messageSnapshot")
                            setAllowDictionary(true)
                            if (user.pro.orFalse()){
                                setPurchase(true)
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }


    fun createUserDocument(email: String){
        val docRef = mFirestore.collection(BuildConfig.VOCAB_PATH_DB).document(email)


        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    // Document exists
                    println("Document already exists.")
                } else {
                    // Document does not exist, create it
                    val userData = hashMapOf(
                        "email" to email,
                        "timeCreated" to System.currentTimeMillis(),
                        "versionApp" to BuildConfig.VERSION_CODE,
                        "versionName" to BuildConfig.VERSION_NAME
                    )
                    docRef.set(userData).addOnSuccessListener {
                        println("Document successfully created.")
                    }.addOnFailureListener { e ->
                        println("Error creating document: $e")
                    }
                }
            } else {
                println("Failed to check document: ${task.exception}")
            }
        }

    }

}

sealed class MainEffect {
    data class SignIn(val intentSender: IntentSender) : MainEffect()
    data class Authorize(val intentSender: IntentSender) : MainEffect()
}

sealed class MainEvent {
    object SignInGoogle : MainEvent()
    object SignOut : MainEvent()

    data class Backup(val imageUri: Uri) : MainEvent()

    data class OnSignInResult(val intent: Intent) : MainEvent()
    data class OnAuthorize(val intent: Intent) : MainEvent()
    data class Restore(val fileId: String) : MainEvent()

    object GetFiles : MainEvent()
}