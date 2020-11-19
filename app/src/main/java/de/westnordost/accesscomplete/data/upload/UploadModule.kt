package de.westnordost.accesscomplete.data.upload

import dagger.Module
import dagger.Provides
import de.westnordost.accesscomplete.ApplicationConstants

@Module
object UploadModule {
    @Provides fun checkVersionIsBanned(): VersionIsBannedChecker =
        VersionIsBannedChecker(
            "https://www.westnordost.de/streetcomplete/banned_versions.txt",
            ApplicationConstants.USER_AGENT
        )

    @Provides fun uploadProgressSource(uploadController: UploadController): UploadProgressSource =
        uploadController
}
