package de.westnordost.accesscomplete

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import de.westnordost.accesscomplete.location.LocationRequestFragment
import de.westnordost.accesscomplete.util.CrashReportExceptionHandler
import de.westnordost.accesscomplete.util.SoundFx
import javax.inject.Singleton


@Module class ApplicationModule(private val application: Application) {

    @Provides fun appContext(): Context = application

    @Provides fun application(): Application = application

    @Provides fun preferences(): SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(application)

    @Provides fun assetManager(): AssetManager = application.assets

    @Provides fun resources(): Resources = application.resources

    @Provides fun locationRequestComponent(): LocationRequestFragment = LocationRequestFragment()

    @Provides @Singleton fun soundFx(): SoundFx = SoundFx(appContext())

    @Provides @Singleton fun exceptionHandler(ctx: Context): CrashReportExceptionHandler =
        CrashReportExceptionHandler(
            ctx,
            "sven.stoll@uzh.ch",
            "crashreport.txt"
        )
}
