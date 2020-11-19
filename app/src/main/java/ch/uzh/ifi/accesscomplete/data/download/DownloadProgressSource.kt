package ch.uzh.ifi.accesscomplete.data.download

interface DownloadProgressSource {
    val isPriorityDownloadInProgress: Boolean
    val isDownloadInProgress: Boolean
    val currentDownloadItem: DownloadItem?

    fun addDownloadProgressListener(listener: DownloadProgressListener)
    fun removeDownloadProgressListener(listener: DownloadProgressListener)
}
