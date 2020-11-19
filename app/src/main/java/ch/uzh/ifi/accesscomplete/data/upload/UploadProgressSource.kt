package ch.uzh.ifi.accesscomplete.data.upload

interface UploadProgressSource {
    val isUploadInProgress: Boolean

    fun addUploadProgressListener(listener: UploadProgressListener)
    fun removeUploadProgressListener(listener: UploadProgressListener)
}
