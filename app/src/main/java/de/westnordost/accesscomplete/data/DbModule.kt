package de.westnordost.accesscomplete.data

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import dagger.Module
import dagger.Provides
import de.westnordost.accesscomplete.ApplicationConstants
import de.westnordost.accesscomplete.util.KryoSerializer
import de.westnordost.accesscomplete.util.Serializer
import javax.inject.Singleton

@Module
object DbModule {
    @Provides @Singleton fun sqLiteOpenHelper(ctx: Context): SQLiteOpenHelper =
        sqLiteOpenHelper(ctx, ApplicationConstants.DATABASE_NAME)

    fun sqLiteOpenHelper(ctx: Context, databaseName: String): SQLiteOpenHelper =
        StreetCompleteSQLiteOpenHelper(ctx, databaseName)

	@Provides @Singleton fun serializer(): Serializer = KryoSerializer()
}
