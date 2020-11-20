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

package ch.uzh.ifi.accesscomplete.data.upload;

import java.util.Arrays;
import java.util.List;

import dagger.Module;
import dagger.Provides;
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.OsmQuestsUploader;
import ch.uzh.ifi.accesscomplete.data.osm.splitway.SplitWaysUploader;
import ch.uzh.ifi.accesscomplete.data.osm.osmquest.undo.UndoOsmQuestsUploader;
import ch.uzh.ifi.accesscomplete.data.osmnotes.createnotes.CreateNotesUploader;
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestsChangesUploader;

@Module
public class UploadModule2
{
	/* NOTE: For some reason, when converting this to Kotlin, Dagger 2 does not find this anymore
	*  and cannot provide the dependency for UploadService. So, it must stay in Java (for now) */
	@Provides public static List<? extends Uploader> uploaders(
		OsmNoteQuestsChangesUploader osmNoteQuestsChangesUploader,
		UndoOsmQuestsUploader undoOsmQuestsUploader, OsmQuestsUploader osmQuestsUploader,
		SplitWaysUploader splitWaysUploader, CreateNotesUploader createNotesUploader
	) {
		return Arrays.asList(osmNoteQuestsChangesUploader, undoOsmQuestsUploader, osmQuestsUploader,
			splitWaysUploader, createNotesUploader);
	}
}
