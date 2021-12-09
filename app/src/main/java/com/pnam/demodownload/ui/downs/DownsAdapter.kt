package com.pnam.demodownload.ui.downs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pnam.demodownload.R
import com.pnam.demodownload.model.Download

class DownsAdapter(
    private val initProgress: (downloadId: Long, position: Int) -> Unit,
    private val detachedView: (downloadId: Long) -> Unit
) : ListAdapter<Download, DownsAdapter.DownsViewHolder>(DIFF) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownsViewHolder {
        return DownsViewHolder.create(parent, viewType, initProgress)
    }

    override fun onBindViewHolder(holder: DownsViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onViewDetachedFromWindow(holder: DownsViewHolder) {
        super.onViewDetachedFromWindow(holder)
        detachedView(getItem(holder.adapterPosition).id)
    }

    class DownsViewHolder private constructor(
        private val itemView: View,
        private val initProgress: (downloadId: Long, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            initView()
        }

        private fun initView() {
            name = itemView.findViewById(R.id.name)
            status = itemView.findViewById(R.id.status)
            progressText = itemView.findViewById(R.id.progress_text)
            progressPercent = itemView.findViewById(R.id.progress_percent)
            progress = itemView.findViewById(R.id.progress)
        }

        @SuppressLint("SetTextI18n")
        fun setProgress(bytesDownloaded: Int, bytesTotal: Int) {
            val progressInt = ((bytesDownloaded.toFloat() / bytesTotal.toFloat()) * 100).toInt()
            progress.progress = progressInt
            progressPercent.text = "$progressInt%"
            if(bytesDownloaded == -1 && bytesTotal == -1) {
                progressText.text = "Done"
            }else{
                progressText.text = "$bytesDownloaded/$bytesTotal"
            }
        }

        fun setDownloadStatus(downloadStatus: String) {
            status.text = downloadStatus
        }

        fun bind(download: Download, position: Int) {
            name.text = download.name
            initProgress(download.id, position)
        }

        private lateinit var name: TextView

        private lateinit var status: TextView

        private lateinit var progressText: TextView

        private lateinit var progressPercent: TextView

        private lateinit var progress: ProgressBar

        companion object {
            fun create(
                parent: ViewGroup,
                viewType: Int,
                initProgress: (downloadId: Long, position: Int) -> Unit
            ): DownsViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_download, parent, false)
                return DownsViewHolder(view, initProgress)
            }
        }
    }

    companion object {
        private val DIFF: DiffUtil.ItemCallback<Download> by lazy {
            object : DiffUtil.ItemCallback<Download>() {
                override fun areItemsTheSame(oldItem: Download, newItem: Download): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Download, newItem: Download): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
}