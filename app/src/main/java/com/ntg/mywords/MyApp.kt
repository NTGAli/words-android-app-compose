package com.ntg.mywords

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber


@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        setTheme(R.style.Theme_MyWord)
        super.onCreate()
        initTimber()
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)



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