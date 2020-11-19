package ch.uzh.ifi.accesscomplete.data.download

import dagger.Module
import dagger.Provides

@Module
object DownloadModule {
    @Provides
    fun downloadProgressSource(downloadController: QuestDownloadController): DownloadProgressSource =
        downloadController
}
