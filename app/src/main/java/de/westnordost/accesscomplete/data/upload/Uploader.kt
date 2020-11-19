package de.westnordost.accesscomplete.data.upload

import java.util.concurrent.atomic.AtomicBoolean

interface Uploader {
    var uploadedChangeListener: OnUploadedChangeListener?

    fun upload(cancelled: AtomicBoolean)
}
