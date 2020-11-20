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

package ch.uzh.ifi.accesscomplete

import ch.uzh.ifi.accesscomplete.data.DbModule
import ch.uzh.ifi.accesscomplete.data.OsmApiModule
import ch.uzh.ifi.accesscomplete.data.download.DownloadModule
import ch.uzh.ifi.accesscomplete.data.meta.MetadataModule
import ch.uzh.ifi.accesscomplete.data.osmnotes.OsmNotesModule
import ch.uzh.ifi.accesscomplete.data.upload.UploadModule
import ch.uzh.ifi.accesscomplete.data.user.UserModule
import ch.uzh.ifi.accesscomplete.data.user.achievements.AchievementsModule
import ch.uzh.ifi.accesscomplete.map.MapModule
import ch.uzh.ifi.accesscomplete.quests.QuestModule

object Injector {

    lateinit var applicationComponent: ApplicationComponent
        private set

    fun initializeApplicationComponent(app: AccessCompleteApplication?) {
        applicationComponent = DaggerApplicationComponent.builder()
            .applicationModule(ApplicationModule(app!!)) // not sure why it is necessary to add these all by hand, I must be doing something wrong
            .achievementsModule(AchievementsModule)
            .dbModule(DbModule)
            .downloadModule(DownloadModule)
            .metadataModule(MetadataModule)
            .osmApiModule(OsmApiModule)
            .osmNotesModule(OsmNotesModule)
            .questModule(QuestModule)
            .uploadModule(UploadModule)
            .userModule(UserModule)
            .mapModule(MapModule)
            .build()
    }
}
