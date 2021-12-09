package com.pnam.demodownload.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.pnam.demodownload.repository.DownloadRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: DownloadRepository by lazy {
        DownloadRepository.create(application)
    }

    private val _downloadId: MutableLiveData<Long> by lazy {
        MutableLiveData()
    }
    val downloadId: MutableLiveData<Long> get() = _downloadId
    private val _downloadIdValue: Long get() = _downloadId.value ?: -1

    fun download(link: String) {
        downloadId.postValue(repository.download(link))
    }

    fun getDownloadStatus(): String {
        return repository.getDownloadStatus(_downloadIdValue).toString()
    }

    companion object {
        const val PUBG_LINK =
            "https://ldcdn.ldmnq.com/download/package/LDPlayer4.0.exe?n=LDPlayer4.0_vn_com.vng.pubgmobile_13319362_ld.exe"
        const val MP3_LINK = "http://commonsware.com/misc/test.mp4"
        const val IMAGE_LINK = "https://scontent.fsgn5-8.fna.fbcdn.net/v/t1.15752-9/254496236_892310944753638_2376090593794509605_n.jpg?_nc_cat=103&ccb=1-5&_nc_sid=ae9488&_nc_ohc=L5aLis5SYyQAX8AhsUF&tn=EQh-q9f_joAhMAj4&_nc_ht=scontent.fsgn5-8.fna&oh=aaf29adcfbf7b09923ef2f50d08a1a9c&oe=61D7BEB3"
    }
}