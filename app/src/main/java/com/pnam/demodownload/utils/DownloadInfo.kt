package com.pnam.demodownload.utils

sealed class DownloadInfo {
    class HasInfo(
        val name: String,
        val status: String,
        val reason: String
    ): DownloadInfo() {
        override fun toString(): String {
            return "Name: $name\nDownload Status: $status, $reason"
        }
    }

    class DontHasInfo: DownloadInfo() {
        override fun toString(): String {
            return "NO_STATUS_INFO"
        }
    }
}
