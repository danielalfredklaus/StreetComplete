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

package ch.uzh.ifi.accesscomplete.data.osm.upload.changesets

import android.content.Context

import javax.inject.Inject

import androidx.work.Worker
import androidx.work.WorkerParameters
import de.westnordost.osmapi.common.errors.OsmAuthorizationException
import de.westnordost.osmapi.common.errors.OsmConnectionException
import ch.uzh.ifi.accesscomplete.Injector

class ChangesetAutoCloserWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    @Inject internal lateinit var openQuestChangesetsManager: OpenQuestChangesetsManager

    init {
        Injector.applicationComponent.inject(this)
    }

    override fun doWork(): Result {
        try {
            openQuestChangesetsManager.closeOldChangesets()
        } catch (e: OsmConnectionException) {
            // wasn't able to connect to the server (i.e. connection timeout). Oh well, then,
            // never mind. Could also retry later with Result.retry() but the OSM API closes open
            // changesets after 1 hour anyway.
        } catch (e: OsmAuthorizationException) {
            // the user may not be authorized yet (or not be authorized anymore) #283
            // nothing we can do about here. He will have to reauthenticate when he next opens the app
            return Result.failure()
        }
        return Result.success()
    }
}
