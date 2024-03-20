package com.ntg.vocabs.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ntg.vocabs.api.ApiService
import com.ntg.vocabs.api.NetworkResult
import com.ntg.vocabs.db.dao.AdHistoryDao
import com.ntg.vocabs.model.db.AdHistory
import com.ntg.vocabs.model.response.FullScreenAd
import com.ntg.vocabs.model.response.RecentMessage
import com.ntg.vocabs.model.response.ResponseBody
import com.ntg.vocabs.util.safeApiCall
import com.ntg.vocabs.util.timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageBoxViewModel @Inject constructor(
    private val api: ApiService,
    private val adHistoryDao: AdHistoryDao
//    private val firebaseDB: FirebaseDatabase
) : ViewModel() {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    val messagesLiveData: MutableLiveData<List<RecentMessage>> = MutableLiveData()
    val fullScreenAd: MutableLiveData<FullScreenAd?> = MutableLiveData()
    private var lastLoad: Long = 0
    private var lastLoadFullScreen: Long = 0

    fun loadMessages() {
        val now = System.currentTimeMillis()
        if (messagesLiveData.value.orEmpty().isNotEmpty() &&
            now - lastLoad < 1800000
        ) return
        val messagesRef = database.child("messages")
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages: MutableList<RecentMessage> = mutableListOf()

                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(RecentMessage::class.java)
                    if (message != null) {
                        lastLoad = System.currentTimeMillis()
                        messages.add(message)
                    }
                }
                messagesLiveData.value = messages
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }


    fun loadFullScreenAd() {
        timber("loadFullScreenAdloadFullScreenAdloadFullScreenAd")
        val now = System.currentTimeMillis()
        if (fullScreenAd.value == null &&
            now - lastLoadFullScreen < 1800000
        ) return
        val messagesRef = database.child("full_screen_ad")
        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                timber("lastLoadFullScreen", snapshot.toString())
//                for (messageSnapshot in snapshot.children) {
                val message = snapshot.getValue(FullScreenAd::class.java)
                if (message != null) {
                    lastLoadFullScreen = System.currentTimeMillis()
                    fullScreenAd.value = message
                }
//                }

            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to read value: ${error.toException()}")
            }
        })
    }


    fun isUserAlreadySeen(id: String): LiveData<AdHistory?> {
        return adHistoryDao.findAd(id)
    }

    fun seenAd(id: String, skipped: Boolean) {
        viewModelScope.launch {
            adHistoryDao.insert(AdHistory(0, id, System.currentTimeMillis().toString(), skipped))
        }
    }

}