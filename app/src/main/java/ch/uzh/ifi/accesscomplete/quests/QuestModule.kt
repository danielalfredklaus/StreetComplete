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

package ch.uzh.ifi.accesscomplete.quests

import dagger.Module
import dagger.Provides
import de.westnordost.osmfeatures.FeatureDictionary
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import ch.uzh.ifi.accesscomplete.quests.incline.AddPathIncline
import ch.uzh.ifi.accesscomplete.quests.incline.AddPedestrianAccessibleStreetIncline
import ch.uzh.ifi.accesscomplete.quests.kerb_type.AddKerbType
import ch.uzh.ifi.accesscomplete.quests.smoothness.AddFootwayPartSmoothness
import ch.uzh.ifi.accesscomplete.quests.smoothness.AddPathSmoothness
import ch.uzh.ifi.accesscomplete.quests.smoothness.AddPedestrianAccessibleStreetSmoothness
import ch.uzh.ifi.accesscomplete.quests.surface.AddCyclewayPartSurface
import ch.uzh.ifi.accesscomplete.quests.surface.AddFootwayPartSurface
import ch.uzh.ifi.accesscomplete.quests.surface.AddPathSurface
import ch.uzh.ifi.accesscomplete.quests.surface.AddPedestrianAccessibleStreetSurface
import ch.uzh.ifi.accesscomplete.quests.width.AddCyclewayPartWidth
import ch.uzh.ifi.accesscomplete.quests.width.AddFootwayPartWidth
import ch.uzh.ifi.accesscomplete.quests.width.AddPathWidth
import ch.uzh.ifi.accesscomplete.quests.width.AddPedestrianAccessibleStreetWidth
import java.util.concurrent.FutureTask
import javax.inject.Singleton

@Module
object QuestModule {

    @Provides
    @Singleton
    fun questTypeRegistry(
        osmNoteQuestType: OsmNoteQuestType,
        featureDictionaryFuture: FutureTask<FeatureDictionary>
    ): QuestTypeRegistry = QuestTypeRegistry(listOf(

        // Notes
        osmNoteQuestType,

        // Kerb
        AddKerbType(),

        // Surface
        AddPathSurface(),
        AddPedestrianAccessibleStreetSurface(),
        AddFootwayPartSurface(),
        AddCyclewayPartSurface(),

        // Smoothness
        AddPathSmoothness(),
        AddPedestrianAccessibleStreetSmoothness(),
        AddFootwayPartSmoothness(),

        // Width
        AddPathWidth(),
        AddPedestrianAccessibleStreetWidth(),
        AddFootwayPartWidth(),
        AddCyclewayPartWidth(),

        // Incline
        AddPathIncline(),
        AddPedestrianAccessibleStreetIncline(),

        // Original StreetComplete quests that are interesting from an accessibility point of view:
//        AddSidewalk(), // for any pedestrian routers
//        MarkCompletedHighwayConstruction(),
//        AddProhibitedForPedestrians(), // uses info from AddSidewalk quest, should be after it
//        AddStepsRamp(),
//        AddWheelchairAccessBusiness(featureDictionaryFuture), // used by wheelmap, OsmAnd, MAPS.ME
//        AddWheelchairAccessToilets(), // used by wheelmap, OsmAnd, MAPS.ME
//        AddWheelchairAccessToiletsPart(),
//        AddWheelchairAccessPublicTransport(),
//        AddWheelchairAccessOutside(),
//        AddTactilePavingCrosswalk(), // Paving can be completed while waiting to cross
//        AddTrafficSignalsSound(), // Sound needs to be done as or after you're crossing
//        AddTrafficSignalsVibration(),
//        AddTrafficSignalsButton()
    ))

    @Provides
    @Singleton
    fun osmNoteQuestType(): OsmNoteQuestType = OsmNoteQuestType()
}
