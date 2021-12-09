package com.pnam.demodownload.ui.downs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.pnam.demodownload.repository.DownloadRepository

class DownsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DownloadRepository by lazy {
        DownloadRepository.create(application)
    }

    fun getDownloadStatus(downloadId: Long): String {
        return repository.getDownloadStatus(downloadId).toString()
    }

    fun downloadProgress(
        downloadId: Long,
        progressHandle: (bytesDownloaded: Int, bytesTotal: Int) -> Unit
    ) {
        repository.downloadProgress(downloadId, progressHandle)
    }

    fun removeProgressDownload(downloadId: Long) {
        repository.removeProgressDownload(downloadId)
    }
}