package com.pnam.demodownload.ui.downs

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.pnam.demodownload.R
import com.pnam.demodownload.model.Download

class DownsActivity : AppCompatActivity() {
    private val vm: DownsViewModel by viewModels()
    private val adapter: DownsAdapter by lazy {
        DownsAdapter({ downloadId, position ->
            vm.downloadProgress(downloadId) { bytesDownloaded, bytesTotal ->
                val holder: DownsAdapter.DownsViewHolder? =
                    downsRecycle.findViewHolderForAdapterPosition(position) as DownsAdapter.DownsViewHolder?
                holder?.let { holder ->
                    runOnUiThread {
                        holder.setProgress(bytesDownloaded, bytesTotal)
                        holder.setDownloadStatus(vm.getDownloadStatus(downloadId))
                    }
                }
            }
        }) { downloadId ->
            vm.removeProgressDownload(downloadId)
        }
    }
    private val downsRecycle: RecyclerView by lazy {
        findViewById(R.id.downs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_downs)
        downsRecycle.adapter = adapter
        adapter.submitList(Download.downloads)
    }
}