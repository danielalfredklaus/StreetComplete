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

package ch.uzh.ifi.accesscomplete;

import androidx.appcompat.app.AppCompatDelegate;

/** Constant class to have all the identifiers for shared preferences in one place */
public class Prefs {

	public static final String
			OAUTH_ACCESS_TOKEN = "oauth.accessToken",
			OAUTH_ACCESS_TOKEN_SECRET = "oauth.accessTokenSecret",
			MAP_TILECACHE_IN_MB = "map.tilecache",
			SHOW_NOTES_NOT_PHRASED_AS_QUESTIONS = "display.nonQuestionNotes",
			AUTOSYNC = "autosync",
			KEEP_SCREEN_ON = "display.keepScreenOn",
			THEME_SELECT = "theme.select",
			RESURVEY_INTERVALS = "quests.resurveyIntervals";

	public static final String
		OSM_USER_ID = "osm.userid",
		OSM_USER_NAME = "osm.username",
		OSM_UNREAD_MESSAGES = "osm.unread_messages",
		USER_DAYS_ACTIVE = "days_active",
		USER_GLOBAL_RANK = "user_global_rank",
		USER_LAST_TIMESTAMP_ACTIVE = "last_timestamp_active",
		IS_SYNCHRONIZING_STATISTICS = "is_synchronizing_statistics";

	// not shown anywhere directly
	public static final String
			QUEST_ORDER = "quests.order",
			LAST_SOLVED_QUEST_TIME = "changesets.lastQuestSolvedTime",
			MAP_LATITUDE = "map.latitude",
			MAP_LONGITUDE = "map.longitude",
			LAST_PICKED_PREFIX = "imageListLastPicked.",
			LAST_LOCATION_REQUEST_DENIED = "location.denied",
			LAST_VERSION = "lastVersion",
			LAST_VERSION_DATA = "lastVersion_data",
			HAS_SHOWN_TUTORIAL = "hasShownTutorial",
			HAS_COMPLETED_ARCORE_MEASUREMENT = "hasCompletedARCoreMeasurement";

	public static final String QUEST_SPRITES_VERSION = "TangramQuestSpriteSheet.version";
	public static final String QUEST_SPRITES = "TangramQuestSpriteSheet.questSprites";

	public enum Autosync
	{
		ON, WIFI, OFF
	}

	public enum Theme
	{
		LIGHT(AppCompatDelegate.MODE_NIGHT_NO),
		DARK(AppCompatDelegate.MODE_NIGHT_YES),
		AUTO(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY),
		SYSTEM(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

		public final int appCompatNightMode;
		Theme(int appCompatNightMode) { this.appCompatNightMode = appCompatNightMode; }
	}

	public enum ResurveyIntervals
	{
		LESS_OFTEN, DEFAULT, MORE_OFTEN
	}
}
