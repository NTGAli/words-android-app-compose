package com.ntg.vocabs.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

class DownloadManagerUtil(private val context: Context) {

    interface DownloadListener {
        fun onDownloadProgress(progress: Int)
        fun onDownloadCompleted()
        fun onDownloadFailed()
    }

    private var downloadManager: DownloadManager? = null
    private var downloadId: Long = -1
    private var downloadListener: DownloadListener? = null

    fun setDownloadListener(listener: DownloadListener) {
        downloadListener = listener
    }

    fun downloadFile(url: String, fileName: String) {
        val request = DownloadManager.Request(Uri.parse(url))

        val appDataDir = context.getExternalFilesDir(null)?.absolutePath
        val destinationPath = File(appDataDir, fileName).absolutePath
        request.setDestinationUri(Uri.parse("file://$destinationPath"))
            .setTitle("Downloading $fileName")
        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager?.enqueue(request) ?: -1

        CoroutineScope(Dispatchers.IO).launch {
            while (true){
                if (checkDownloadProgress()) break
                checkDownloadProgress()
                checkDownloadStatus()
                delay(1000)
            }
        }
    }

    private fun checkDownloadProgress(): Boolean {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager?.query(query)
        var progress = 0

        if (cursor != null && cursor.moveToFirst()) {
            val bytesDownloaded = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
            val bytesTotal = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))

            if (bytesTotal > 0) {
                progress = ((bytesDownloaded * 100) / bytesTotal).toInt()
                downloadListener?.onDownloadProgress(progress)
            }

//            cursor.close()
        }

        return progress == 100
    }

    private fun checkDownloadStatus() {
        val query = DownloadManager.Query().setFilterById(downloadId)
        val cursor = downloadManager?.query(query)

        if (cursor != null && cursor.moveToFirst()) {
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                downloadListener?.onDownloadCompleted()
            } else if (status == DownloadManager.STATUS_FAILED) {
                downloadListener?.onDownloadFailed()
            }
        }
    }
}
