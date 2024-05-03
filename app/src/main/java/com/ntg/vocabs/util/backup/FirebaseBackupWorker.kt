package com.ntg.vocabs.util.backup

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.db.dao.WordDao
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.model.db.toMap
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_MEDIA
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_TIMES
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.orFalse
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

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
                                timber("addOnFailureListener ::: www :::: $word")

                                if (word.fid != null){
                                    deleteOnFirestore(word.fid.orEmpty(), type) {
                                        timber("addOnFailureListener ::: vvv :::: $it")

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
                        timber("getUnSyncedWords :::: $it")

                        it.forEach { list ->
                            if (list.isDeleted.orFalse()) {
                                timber("addOnFailureListener ::: www :::: $list")

                                if (list.fid != null){
                                    deleteOnFirestore(list.fid, type) {
                                        timber("addOnFailureListener ::: vvv :::: $it")

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
                    appDB.timeSpentDao().getUnSyncedTime().let {
                        timber("getUnSyncedWords :::: $it")

                        it.forEach { time ->

                            if (time.isDeleted.orFalse() && time.fid != null) {
                                deleteOnFirestore(time.fid, type) {
                                    if (it) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            appDB.timeSpentDao().delete(time)
                                        }
                                    }
                                }
                            }else if (time.fid == null) {
                                backupOnFirestore(time.apply {
                                    this.email = email
                                    this.synced = true
                                }, type) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        appDB.timeSpentDao().synced(time.id, it.id)
                                    }
                                }
                            } else {
                                updateBackupOnFirestore(time.apply {
                                    this.email = email
                                    this.synced = true
                                }.toMap(),time.fid, type) {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        appDB.timeSpentDao().synced(time.id)
                                    }
                                }
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

        firestore.collection(type).add(data)
            .addOnSuccessListener {
                CoroutineScope(Dispatchers.IO).launch {
                    isSuccess.invoke(it)
                }

            }
            .addOnFailureListener { e -> timber("BBBCCCBB ::: Error writing document :::: $e") }

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
        timber("addOnFailureListener ::: ............")

        val firestore = Firebase.firestore
        firestore.collection(type).document(fid)
            .delete()
            .addOnSuccessListener {
                timber("addOnFailureListener ::: ---------")
                isSuccess.invoke(true) }
            .addOnFailureListener { e ->
                timber("addOnFailureListener ::: ${e.message}")
                isSuccess.invoke(false) }
    }
}