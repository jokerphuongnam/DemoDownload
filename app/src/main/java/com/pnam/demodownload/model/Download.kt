package com.pnam.demodownload.model

data class Download(
    val id: Long,
    val name: String
) {
    companion object {
        val downloads: MutableList<Download> by lazy {
            mutableListOf()
        }
    }
}