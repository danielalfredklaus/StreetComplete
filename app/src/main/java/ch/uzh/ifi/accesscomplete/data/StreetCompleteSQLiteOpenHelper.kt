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

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ch.uzh.ifi.accesscomplete.data.download.tiles.DownloadedTilesTable
import ch.uzh.ifi.accesscomplete.data.notifications.NewUserAchievementsTable
import ch.uzh.ifi.accesscomplete.data.osm.elementgeometry.ElementGeometryTable
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.NodeTable
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.RelationTable
import ch.uzh.ifi.accesscomplete.data.osm.mapdata.WayTable
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestTable
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestTable
import ch.uzh.ifi.accesscomplete.data.osm.splitway.OsmQuestSplitWayTable
import ch.uzh.ifi.accesscomplete.data.osm.upload.changesets.OpenChangesetsTable
import ch.uzh.ifi.accesscomplete.data.osmnotes.NoteTable
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNoteTable
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestTable
import ch.uzh.ifi.accesscomplete.data.user.CountryStatisticsTable
import ch.uzh.ifi.accesscomplete.data.user.QuestStatisticsTable
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserAchievementsTable
import ch.uzh.ifi.accesscomplete.data.user.achievements.UserLinksTable
import ch.uzh.ifi.accesscomplete.data.visiblequests.QuestVisibilityTable
import javax.inject.Singleton

@Singleton class StreetCompleteSQLiteOpenHelper(context: Context, dbName: String) :
    SQLiteOpenHelper(context, dbName, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(ElementGeometryTable.CREATE)
        db.execSQL(OsmQuestTable.CREATE)

        db.execSQL(UndoOsmQuestTable.CREATE)

        db.execSQL(NodeTable.CREATE)
        db.execSQL(WayTable.CREATE)
        db.execSQL(RelationTable.CREATE)

        db.execSQL(NoteTable.CREATE)
        db.execSQL(OsmNoteQuestTable.CREATE)
        db.execSQL(CreateNoteTable.CREATE)

        db.execSQL(QuestStatisticsTable.CREATE)
        db.execSQL(CountryStatisticsTable.CREATE)
        db.execSQL(UserAchievementsTable.CREATE)
        db.execSQL(UserLinksTable.CREATE)
        db.execSQL(NewUserAchievementsTable.CREATE)

        db.execSQL(DownloadedTilesTable.CREATE)

        db.execSQL(OsmQuestTable.CREATE_VIEW)
        db.execSQL(UndoOsmQuestTable.MERGED_VIEW_CREATE)
        db.execSQL(OsmNoteQuestTable.CREATE_VIEW)

        db.execSQL(OpenChangesetsTable.CREATE)

        db.execSQL(QuestVisibilityTable.CREATE)

        db.execSQL(OsmQuestSplitWayTable.CREATE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // for later changes to the DB
        // ...
    }
}

private const val DB_VERSION = 18
