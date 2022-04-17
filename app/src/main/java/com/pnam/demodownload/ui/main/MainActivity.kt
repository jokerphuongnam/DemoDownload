package com.pnam.demodownload.ui.main

import android.app.DownloadManager
import android.content.*
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.chip.Chip
import com.pnam.demodownload.R
import com.pnam.demodownload.background.service.ShowDownloadNotificationService
import com.pnam.demodownload.ui.downs.DownsActivity


class MainActivity : AppCompatActivity() {
    private val vm: MainViewModel by viewModels()

    private val download: Chip by lazy {
        findViewById(R.id.download)
    }

    private val status: TextView by lazy {
        findViewById(R.id.status)
    }

    private val downsActivity: Button by lazy {
        findViewById(R.id.downs_activity)
    }

    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            status.isVisible = true
            status.text = vm.getDownloadStatus()
            vm.downloadId.postValue(null)
        }
    }

    private var onNotificationClick: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        download.setOnClickListener {
            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)

            val listItems = arrayOf(
                "PUBG" to MainViewModel.PUBG_LINK,
                "Mp3" to MainViewModel.MP3_LINK,
                "Image" to MainViewModel.IMAGE_LINK
            )

            alertDialog.setSingleChoiceItems(
                listItems.map { it.first }.toTypedArray(),
                -1
            ) { dialog, which -> // update the selected item which is selected by the user
                vm.download(listItems[which].second)
                registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
                registerReceiver(
                    onNotificationClick,
                    IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED)
                )
                dialog.dismiss()
            }

            alertDialog.setNegativeButton("Cancel") { _, _ -> }

            val customAlertDialog: AlertDialog = alertDialog.create()
            customAlertDialog.show()
        }
        downsActivity.setOnClickListener {
            startActivity(Intent(applicationContext, DownsActivity::class.java))
        }
        vm.downloadId.observe(this) { id ->
            if (id == null || id == -1L) {
//                status.isVisible = false
            } else {
                status.isVisible = true
                status.text = vm.getDownloadStatus()
                startService(Intent(this@MainActivity, ShowDownloadNotificationService::class.java).also { intent ->
                    intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, id)
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
        unregisterReceiver(onNotificationClick)
    }
}