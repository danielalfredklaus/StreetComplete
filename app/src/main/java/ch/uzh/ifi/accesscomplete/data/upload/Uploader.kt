package ch.uzh.ifi.accesscomplete.data.upload

import java.util.concurrent.atomic.AtomicBoolean

interface Uploader {
    var uploadedChangeListener: OnUploadedChangeListener?

    fun upload(cancelled: AtomicBoolean)
}
