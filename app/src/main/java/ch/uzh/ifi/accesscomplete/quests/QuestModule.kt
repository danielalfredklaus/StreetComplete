package ch.uzh.ifi.accesscomplete.quests

import dagger.Module
import dagger.Provides
import de.westnordost.osmfeatures.FeatureDictionary
import ch.uzh.ifi.accesscomplete.data.osmnotes.notequests.OsmNoteQuestType
import ch.uzh.ifi.accesscomplete.data.quest.QuestTypeRegistry
import ch.uzh.ifi.accesscomplete.quests.incline.AddPathIncline
import ch.uzh.ifi.accesscomplete.quests.incline.AddPedestrianAccessibleStreetIncline
import ch.uzh.ifi.accesscomplete.quests.kerb_type.AddKerbType
import ch.uzh.ifi.accesscomplete.quests.smoothness.AddCyclewayPartSmoothness
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
        AddCyclewayPartSmoothness(),

        // Width
        AddPathWidth(),
        AddPedestrianAccessibleStreetWidth(),
        AddFootwayPartWidth(),
        AddCyclewayPartWidth(),

        // Incline
        AddPathIncline(),
        AddPedestrianAccessibleStreetIncline(),

//        AddCycleway(), // for any cyclist routers (and cyclist maps)
//        AddSidewalk(), // for any pedestrian routers
//        MarkCompletedHighwayConstruction(),
//        AddProhibitedForPedestrians(), // uses info from AddSidewalk quest, should be after it
//        AddStepsRamp(),
//        AddCyclewaySegregation(),
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
