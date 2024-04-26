package com.ntg.vocabs.util.backup

import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ntg.vocabs.db.AppDB
import com.ntg.vocabs.db.dao.WordDao
import com.ntg.vocabs.model.db.Word
import com.ntg.vocabs.util.Constant
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_LISTS
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_MEDIA
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_TIMES
import com.ntg.vocabs.util.Constant.BackTypes.BACKUP_WORDS
import com.ntg.vocabs.util.timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FirebaseBackupWorker(
    private val appContext: Context,
    private val workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {


    override suspend fun doWork(): Result {
        val appDB = Room.databaseBuilder(
            context = appContext, AppDB::class.java, Constant.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        val email = inputData.getString("email")
        val type = inputData.getString("type")

        when(type){

            BACKUP_WORDS -> {
                appDB.wordDao().getUnSyncedWords().let {
                    timber("getUnSyncedWords :::: $it")

                    it.forEach {word ->
                        backupOnFirestore(word.apply { this.email = email }, type){
                            CoroutineScope(Dispatchers.IO).launch {
                                appDB.wordDao().synced(word.id)
                            }
                        }
                    }


                }
            }

            BACKUP_LISTS -> {
                appDB.vocabListDao().getUnSyncedLists().let {
                    timber("getUnSyncedWords :::: $it")

                    it.forEach {list ->
                        backupOnFirestore(list.apply { this.email = email }, type){
                            CoroutineScope(Dispatchers.IO).launch {
                                appDB.vocabListDao().synced(list.id)
                            }
                        }
                    }
                }
            }

            BACKUP_TIMES -> {
                appDB.timeSpentDao().getUnSyncedTime().let {
                    timber("getUnSyncedWords :::: $it")

                    it.forEach {list ->
                        backupOnFirestore(list.apply { this.email = email }, type){
                            CoroutineScope(Dispatchers.IO).launch {
                                appDB.timeSpentDao().synced(list.id)
                            }
                        }
                    }
                }
            }

            BACKUP_MEDIA -> {

            }

        }



        return Result.success()
    }

    private suspend fun backupOnFirestore(data: Any, type: String, isSuccess:(Boolean) -> Unit) {
        timber("TTTTTTTTTT :::: $data")
        val firestore = Firebase.firestore

        firestore.collection(type).add(data)
            .addOnSuccessListener {
                timber("BBBCCCBB ::: DocumentSnapshot successfully written!")
                CoroutineScope(Dispatchers.IO).launch {
                    isSuccess.invoke(true)
                }

            }
            .addOnFailureListener { e -> timber("BBBCCCBB ::: Error writing document :::: $e") }

    }

}