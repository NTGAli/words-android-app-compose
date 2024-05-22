package com.ntg.vocabs.util.worker

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.model.db.TimeSpent
import com.ntg.vocabs.model.db.toMap
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_MEDIA
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_TIMES
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.orDefault
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class FirebaseBackupWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {



    override suspend fun doWork(): Result {
        val appDB = Room.databaseBuilder(
            context = appContext, AppDB::class.java, Constant.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        val email = inputData.getString("email")


        if (email.orEmpty().isNotEmpty()){
            when (val type = inputData.getString("type")) {

                BACKUP_WORDS -> {
                    appDB.wordDao().getUnSyncedWords().let {

                        it.forEach { word ->
                            if (word.isDeleted.orFalse()) {

                                if (word.fid != null){
                                    deleteOnFirestore(word.fid.orEmpty(), type) {
                                        if (it) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                appDB.wordDao().delete(word)
                                            }
                                        }
                                    }
                                }else{
                                    appDB.wordDao().delete(word)
                                }
                            } else if (word.fid == null) {
                                backupOnFirestore(word.apply {
                                    this.email = email
                                    this.synced = true
                                }, type) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        appDB.wordDao().synced(word.id, it.id)
                                    }
                                }
                            } else {
                                updateBackupOnFirestore(word.apply {
                                    this.email = email
                                    this.synced = true
                                }.toMap(), word.fid.orEmpty(), type) { isSuccesed ->
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (isSuccesed) {
                                            appDB.wordDao().synced(word.id)
                                        }
                                    }
                                }
                            }
                        }


                    }
                }

                BACKUP_LISTS -> {
                    appDB.vocabListDao().getUnSyncedLists().let {
                        it.forEach { list ->
                            if (list.isDeleted.orFalse()) {
                                if (list.fid != null){
                                    deleteOnFirestore(list.fid, type) {
                                        if (it) {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                appDB.vocabListDao().delete(list)
                                            }
                                        }
                                    }
                                }else{
                                    appDB.vocabListDao().delete(list)
                                }
                            }else if (list.fid == null) {
                                backupOnFirestore(list.apply {
                                    this.email = email
                                    this.synced = true
                                }, type) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        appDB.vocabListDao().synced(list.id, it.id)
                                    }
                                }
                            } else {
                                updateBackupOnFirestore(list.apply {
                                    this.email = email
                                    this.synced = true
                                }.toMap(),list.fid, type) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        appDB.vocabListDao().synced(list.id)
                                    }
                                }
                            }
                        }
                    }
                }

                BACKUP_TIMES -> {
                    val dataOfToday = LocalDate.now().toString()
                    var totalMill = 0L
                    appDB.timeSpentDao().getUnSyncedTime(dataOfToday).groupBy { it.date }.forEach { time ->

                        time.value.groupBy { it.listId }.forEach {spendInList ->

                            spendInList.value.groupBy { it.type }.forEach {spendInType ->

                                spendInType.value.forEach { spendTime ->

                                    if (spendTime.isDeleted.orFalse()){
                                        if (spendTime.fid != null){
                                            deleteOnFirestore(spendTime.fid, type) {
                                                if (it) {
                                                    CoroutineScope(Dispatchers.IO).launch {
                                                        appDB.timeSpentDao().delete(spendTime)
                                                    }
                                                }
                                            }
                                        }else{
                                            CoroutineScope(Dispatchers.IO).launch {
                                                appDB.timeSpentDao().delete(spendTime)
                                            }
                                        }
                                    }else{
                                        if (spendTime.endUnix != null && spendTime.startUnix != null){
                                            totalMill += (spendTime.endUnix.orDefault() - spendTime.startUnix.orDefault())
                                        }
                                    }


                                }

                                val finalMill = totalMill
                                backupOnFirestore(
                                    TimeSpent(
                                        listId = spendInList.key,
                                        synced = true,
                                        email = email,
                                        date = time.key!!,
                                        id = 0,
                                        startUnix = 0,
                                        endUnix = finalMill,
                                        type = spendInType.key
                                    )
                                    , type) {ref ->

                                    spendInList.value.forEach {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            appDB.timeSpentDao().synced(it.id, ref.id)
                                        }
                                    }
                                }
                                totalMill = 0

                            }

                        }

                    }
                }

                BACKUP_MEDIA -> {

                }

            }
        }




        return Result.success()
    }

    private suspend fun backupOnFirestore(
        data: Any,
        type: String,
        isSuccess: (DocumentReference) -> Unit
    ) {
        val firestore = Firebase.firestore

        firestore
            .collection(type).add(data)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    isSuccess.invoke(it)
                }

            }
            .addOnFailureListener { e -> timber("backupOnFirestore ::: Error writing document :::: $e") }

    }

    private suspend fun updateBackupOnFirestore(
        data: Map<String, Any>,
        fid: String,
        type: String,
        isSuccess: (Boolean) -> Unit
    ) {
        val firestore = Firebase.firestore
        firestore.collection(type).document(fid)
            .update(data)
            .addOnCompleteListener {
                isSuccess.invoke(true)
            }
            .addOnFailureListener { e ->
                isSuccess.invoke(false)
            }
    }


    private fun deleteOnFirestore(fid: String, type: String, isSuccess: (Boolean) -> Unit) {
        val firestore = Firebase.firestore
        firestore.collection(type).document(fid)
            .delete()
            .addOnSuccessListener {
                isSuccess.invoke(true) }
            .addOnFailureListener { e ->
                isSuccess.invoke(false) }
    }
}