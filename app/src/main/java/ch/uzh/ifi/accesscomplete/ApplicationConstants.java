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

public class ApplicationConstants {

	public final static String
		NAME = "AccessComplete",
		USER_AGENT = NAME + " " + BuildConfig.VERSION_NAME,
		QUESTTYPE_TAG_KEY = NAME + ":quest_type";

	public final static double
		MAX_DOWNLOADABLE_AREA_IN_SQKM = 12.0,
		MIN_DOWNLOADABLE_AREA_IN_SQKM = 0.1;

	public final static String DATABASE_NAME = "accesscomplete.db";

	public final static int QUEST_TILE_ZOOM = 16;

	public final static int NOTE_MIN_ZOOM = 15;

	/** a "best before" duration for quests. Quests will not be downloaded again for any tile
	 *  before the time expired */
	public static final long REFRESH_QUESTS_AFTER = 3L*24*60*60*1000; // 3 days in ms
	/** the duration after which quests (and quest meta data) will be deleted from the database if
	 *  unsolved and not refreshed in the meantime */
	public static final long DELETE_UNSOLVED_QUESTS_AFTER = 14*24*60*60*1000; // 14 days in ms

	/** the max age of the undo history - one cannot undo changes older than X */
	public static final long MAX_QUEST_UNDO_HISTORY_AGE = 24*60*60*1000; // 1 day in ms

	public static final String AVATARS_CACHE_DIRECTORY = "osm_user_avatars";

	public static final String SC_PHOTO_SERVICE_URL = "https://westnordost.de/streetcomplete/photo-upload/"; // must have trailing /

	public static final int ATTACH_PHOTO_QUALITY = 80;
	public static final int ATTACH_PHOTO_MAXWIDTH = 1280; // WXGA
	public static final int ATTACH_PHOTO_MAXHEIGHT = 1280; // WXGA

	public static final String NOTIFICATIONS_CHANNEL_DOWNLOAD = "downloading";
}
