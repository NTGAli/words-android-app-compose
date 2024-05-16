package com.ntg.vocabs.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.ntg.vocabs.db.dao.TimeSpentDao
import com.ntg.vocabs.model.db.TimeSpent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//class AppLifecycleObserver(private val sessionTimeDao: TimeSpentDao, type: Int) : LifecycleObserver {
//    private var startTime: Long = 0
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_START)
//    fun onEnterForeground() {
//        startTime = System.currentTimeMillis()
//    }
//
//    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
//    fun onEnterBackground() {
//        val endTime = System.currentTimeMillis()
//        val sessionTime = TimeSpent(startTime = startTime, endTime = endTime)
//        CoroutineScope(Dispatchers.IO).launch {
//            sessionTimeDao.insert(sessionTime)
//        }
//    }
//}
