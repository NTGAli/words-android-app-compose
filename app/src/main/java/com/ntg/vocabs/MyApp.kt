package com.ntg.vocabs

import android.app.Application
import android.text.TextUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.ntg.vocabs.db.dao.EnglishWordDao
import com.ntg.vocabs.util.timber
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class MyApp: Application() {

    @Inject
    lateinit var englishWordDao: EnglishWordDao

    override fun onCreate() {
        setTheme(R.style.Theme_Vocabs)
        super.onCreate()
        initTimber()
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                timber("FCM_TOKEN ::::: $token")
            } else {
                timber("FCM_TOKEN ::::: NULL TOKEN")
            }
        }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                try {
                    timber("FCM_TOKEN_FAIL ::::: ${task.result}")
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }
    }

    private fun runtimeEnableAutoInit() {
        // [START fcm_runtime_enable_auto_init]
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        // [END fcm_runtime_enable_auto_init]
    }

    private suspend fun getAndStoreRegToken(): String {
        return Firebase.messaging.token.await()
    }
//    override fun setTheme(resid: Int) {
//        val dataStore = UserStore(this)
//        dataStore.getAccessToken.asLiveData().observeForever {
//            when (it) {
//                getString(R.string.light_mode) -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                }
//                getString(R.string.dark_mode) -> {
//                    timber("wajhdlawfjhalwfjkhwkjahfkwajhf $it")
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                }
//                else -> {
//                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
//                }
//            }
//        }
//
//        super.setTheme(resid)
//
//    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }

}