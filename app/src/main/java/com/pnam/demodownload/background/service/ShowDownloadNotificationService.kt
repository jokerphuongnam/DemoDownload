package com.pnam.demodownload.background.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.pnam.demodownload.R
import com.pnam.demodownload.repository.DownloadRepository
import com.pnam.demodownload.ui.downs.DownsActivity
import com.pnam.demodownload.utils.DownloadInfo

class ShowDownloadNotificationService : Service() {
    private val repository: DownloadRepository by lazy {
        DownloadRepository.create(application)
    }
    private var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            when (val status = repository.getDownloadStatus(referenceId)) {
                is DownloadInfo.HasInfo -> {
                    _notificationBuilder.setContentTitle(status.name)
                        .setContentText("Download complete, Tap to view")
//                      .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .setOngoing(false)
                        .setProgress(0, 0, false)
                }
                is DownloadInfo.DontHasInfo -> {
                    _notificationBuilder.setAutoCancel(true)
                        .setOngoing(false)
                        .setProgress(0, 0, false)
                    stopSelf()
                }
            }

            with(NotificationManagerCompat.from(this@ShowDownloadNotificationService)) {
                notify(referenceId.toInt(), _notificationBuilder.build())
            }
        }
    }

    private var onNotificationClick: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private lateinit var _notificationBuilder: NotificationCompat.Builder

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            .takeIf { downloadId ->
                downloadId != -1L
            }?.let { downloadId ->
                with(NotificationManagerCompat.from(this)) {
                    notify(downloadId.toInt(), _notificationBuilder.build())
                }
                repository.downloadProgress(downloadId) { bytesDownloaded, bytesTotal ->
                    updateNotificationProgress(downloadId, bytesDownloaded, bytesTotal)
                }
            }
        return START_REDELIVER_INTENT
    }

    override fun onCreate() {
        super.onCreate()
        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        registerReceiver(
            onNotificationClick,
            IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED)
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        _notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setChannelId(CHANNEL_ID)
            .setContentTitle("Extracting link")
            .setContentText("Please wait")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
            .setColor(resources.getColor(R.color.cardview_light_background))
            .setAutoCancel(false)
//            .addAction(R.drawable.ic_launcher_background, "Pause", pendingIntentPause)
//            .addAction(R.drawable.ic_launcher_foreground, "Cancel", pendingIntentCancel)
            .setContentIntent(
                PendingIntent.getActivity(
                    this,
                    1233,
                    Intent(
                        this,
                        DownsActivity::class.java
                    ),
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
                    Bundle()
                )
            )
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setProgress(0, 0, true)
    }

    private fun updateNotificationProgress(
        downloadId: Long,
        bytesDownloaded: Int,
        bytesTotal: Int
    ) {
        val progress: Int = ((bytesDownloaded.toFloat() / bytesTotal.toFloat()) * 100).toInt()
        _notificationBuilder.setContentTitle("Downloading...")
            .setContentText("$progress% downloaded: $bytesDownloaded/$bytesTotal")
            .setProgress(100, progress, false)

        with(NotificationManagerCompat.from(this)) {
            notify(downloadId.toInt(), _notificationBuilder.build())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_NONE
        )
        channel.lightColor = Color.BLUE
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        NotificationManagerCompat.from(this).createNotificationChannel(channel)
        return CHANNEL_ID
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(onComplete)
        unregisterReceiver(onNotificationClick)
    }

    companion object {
        const val CHANNEL_ID: String = "DOWNLOAD_SERVICE_CHANNEL"
    }
}