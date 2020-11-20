/*
 * AccessComplete, an easy to use editor of accessibility related
 * OpenStreetMap data for Android.  This program is a fork of
 * StreetComplete (https://github.com/westnordost/StreetComplete).
 *
 * Copyright (C) 2016-2020 Tobias Zwick and contributors (StreetComplete authors)
 * Copyright (C) 2020 Sven Stoll (AccessComplete author)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ch.uzh.ifi.accesscomplete.data

import android.database.sqlite.SQLiteOpenHelper
import androidx.test.platform.app.InstrumentationRegistry
import ch.uzh.ifi.accesscomplete.util.KryoSerializer
import ch.uzh.ifi.accesscomplete.util.Serializer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

open class ApplicationDbTestCase {
    protected lateinit var dbHelper: SQLiteOpenHelper
    protected lateinit var serializer: Serializer

    @Before fun setUpHelper() {
        serializer = KryoSerializer()
        dbHelper = DbModule.sqLiteOpenHelper(
                InstrumentationRegistry.getInstrumentation().targetContext,
                DATABASE_NAME
        )
    }

    @Test fun databaseAvailable() {
        Assert.assertNotNull(dbHelper.readableDatabase)
    }

    @After fun tearDownHelper() {
        dbHelper.close()
        InstrumentationRegistry.getInstrumentation().targetContext
            .deleteDatabase(DATABASE_NAME)
    }

    companion object {
        private const val DATABASE_NAME = "streetcomplete_test.db"
    }
}
