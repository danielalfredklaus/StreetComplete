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
