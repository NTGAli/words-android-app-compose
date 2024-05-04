package com.ntg.vocabs

import androidx.work.DelegatingWorkerFactory
import android.app.Application
import android.text.TextUtils
import androidx.work.Configuration
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.db.AutoInsertWorkerFactory
import com.ntg.vocabs.db.dao.EnglishWordDao
import com.ntg.vocabs.util.timber
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject
    lateinit var englishWordDao: EnglishWordDao

    @Inject
    lateinit var appDB: AppDB

    override fun onCreate() {
        setTheme(R.style.Theme_Vocabs)
        super.onCreate()
        initTimber()
        fcmCheck()
        initPurchase()
    }

    private fun initPurchase(){
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(
                context = this,
                apiKey= "goog_DJihKMxRlxZoobNaHtfljBaJWCW"
            ).build()
        )
    }

    private fun fcmCheck(){
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String ->
            if (!TextUtils.isEmpty(token)) {
                timber("FCM_TOKEN . ::::: $token")
//                saveFCM(token)
            } else {
                timber("FCM_TOKEN . ::::: NULL TOKEN")
            }
        }.addOnFailureListener { e: Exception? -> }.addOnCanceledListener {}
            .addOnCompleteListener { task: Task<String> ->
                try {
                    timber("FCM_TOKEN_FAIL . ::::: ${task.result}")
//                    saveFCM(task.result)
                }catch (e: Exception){
                    e.printStackTrace()
                    timber("FCM_TOKEN_FAIL ::: ${e.message} --------- ${e.printStackTrace()}")
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

    override val workManagerConfiguration: Configuration
        get() {
            val myWorkerFactory = DelegatingWorkerFactory()
            myWorkerFactory.addFactory(AutoInsertWorkerFactory(appDB))
//
//            return Configuration.Builder()
//                .setWorkerFactory(myWorkerFactory)
//                .build()

            return Configuration.Builder().setWorkerFactory(myWorkerFactory).build()
        }


}